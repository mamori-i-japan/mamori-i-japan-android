package jp.mamori_i.app.screen.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.mamori_i.app.R
import jp.mamori_i.app.extension.handleError
import jp.mamori_i.app.screen.home.HomeStatus.HomeStatusType.*
import jp.mamori_i.app.screen.menu.MenuActivity
import jp.mamori_i.app.screen.trace.TraceDataUploadActivity
import jp.mamori_i.app.screen.trace.TraceHistoryActivity
import jp.mamori_i.app.ui.ProgressHUD
import jp.mamori_i.app.util.BLEUtil
import kotlinx.android.synthetic.main.activity_home.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class HomeActivity: AppCompatActivity(), HomeNavigator {
    companion object {
        const val KEY = "jp.mamori_i.app.screen.home.HomeActivity"
    }

    private val viewModel: HomeViewModel by viewModel()
    private val disposable: CompositeDisposable by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初期設定
        initialize()
        // viewの初期設定
        setupViews()
        // viewModelとのbind
        bind()
        // BLE開始
        BLEUtil.startBluetoothMonitoringService(this)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume(this)
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

    override fun onBackPressed() {
        // バックキー押下時はアプリをバックグラウンドに落とす
        moveTaskToBack(true)
    }

    private fun initialize() {
        setContentView(R.layout.activity_home)
        viewModel.navigator = this
    }

    private fun setupViews() {
        cardView.setCardViewEventListener(object : HomeCardView.HomeCardViewEventListener {
            override fun onClickDeepContactCountArea() {
                viewModel.onClickDeepContactCount()
            }
        })

        // TODO 画面切り替え実験用
        usualButton.setOnClickListener {
            viewModel.homeStatus.onNext(HomeStatus(Usual, 10, Date().time))
        }
        semiUsualButton.setOnClickListener {
            viewModel.homeStatus.onNext(HomeStatus(SemiUsual, 25, Date().time))
        }
        deepContactButton.setOnClickListener {
            viewModel.homeStatus.onNext(HomeStatus(DeepContact, 11, Date().time))
        }
        positiveButton.setOnClickListener {
            viewModel.homeStatus.onNext(HomeStatus(Positive, 11, Date().time))
        }

        // TODO 開発用ボタン等なので適当に繋いでいる
        debugButton.setOnClickListener {
            val intent = Intent(this, BLEActivity::class.java)
            this.startActivity(intent)
        }

        menuButton.setOnClickListener {
            viewModel.onClickMenuButton()
        }
    }

    private fun bind() {
        // ホーム画面の表示内容
        viewModel.homeStatus
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { status ->
                // 背景画像の更新
                updateBackgroundImage(status)

                // カード部分の更新
                cardView.updateContent(status)

                // リンクエリアの切り替え
                contentContainerView.removeAllViews()
                contentContainerView.addView(createContentView(status))

                // コンテントビューを表示
                mainContentView.visibility = View.VISIBLE
            }
            .addTo(disposable)

        viewModel.error
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { error ->
                handleError(error)
            }.addTo(disposable)
    }

    private fun updateBackgroundImage(homeStatus: HomeStatus) {
        backgroundImageView.setImageResource( when (homeStatus.statusType) {
            Usual -> R.drawable.img_home_bg_usual
            SemiUsual -> R.drawable.img_home_bg_semi_usual
            DeepContact ->  R.drawable.img_home_bg_deep_contact
            Positive ->  R.drawable.img_home_bg_positive
        })
    }

    private fun createContentView(homeStatus: HomeStatus): View {
        return when (homeStatus.statusType) {
            Usual,
            SemiUsual,
            DeepContact -> {
                HomeNoPositiveContentView(this).apply {
                    listener = object : HomeNoPositiveContentView.HomeNoPositiveContentViewEventListener {
                        override fun onClickStayHomeButton() {
                            viewModel.onClickStayHomeButton()
                        }

                        override fun onClickHygieneButton() {
                            viewModel.onClickHygieneButton()
                        }

                        override fun onClickContactButton() {
                            viewModel.onClickContactButton()
                        }

                        override fun onClickPositiveReport() {
                            viewModel.onClickPositiveReportButton()
                        }

                        override fun onClickShareButton() {
                            viewModel.onClickShareButton()
                        }
                    }
                }
            }
            Positive -> {
                HomePositiveContentView(this).apply {
                    listener = object : HomePositiveContentView.HomePositiveContentViewEventListener {
                        override fun onClickShareButton() {
                            viewModel.onClickShareButton()
                        }
                    }
                }
            }
        }
    }

    override fun showProgress() {
        ProgressHUD.show(this)
    }

    override fun hideProgress() {
        ProgressHUD.hide()
    }

    override fun goToMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        this.startActivity(intent)
    }

    override fun goToTraceHistory() {
        val intent = Intent(this, TraceHistoryActivity::class.java)
        this.startActivity(intent)
    }

    override fun goToPositiveReport() {
        val intent = Intent(this, TraceDataUploadActivity::class.java)
        this.startActivity(intent)
    }

    override fun openWebBrowser(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        this.startActivity(intent)
    }

    override fun openShareComponent(title: String, content: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, content)
        }
        startActivity(
            Intent.createChooser(
                intent,
                title
            )
        )
    }
}