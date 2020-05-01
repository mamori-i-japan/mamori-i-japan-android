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
import jp.mamori_i.app.data.model.UserStatus
import jp.mamori_i.app.data.model.UserStatus.UserStatusType.SemiUsual
import jp.mamori_i.app.data.model.UserStatus.UserStatusType.Usual
import jp.mamori_i.app.screen.menu.MenuActivity
import jp.mamori_i.app.screen.trace.TraceHistoryActivity
import jp.mamori_i.app.ui.ProgressHUD
import jp.mamori_i.app.util.BLEUtil
import kotlinx.android.synthetic.main.activity_home.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


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
        // BLE開始 // TODO 場所の見直し
        BLEUtil.startBluetoothMonitoringService(this)
    }

    override fun onResume() {
        super.onResume()
        // ステータスチェック開始
        viewModel.doUserStatusCheck(this)
        // TODO 組織コードのやつ
        // TODO アプリステータスチェック
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
        cardView.listener = object : HomeCardView.HomeCardViewEventListener {
            override fun onClickDeepContactCountArea() {
                viewModel.onClickDeepContactCount()
            }
        }

        // TODO 画面切り替え実験用
        usualButton.setOnClickListener {
            viewModel.userStatus.onNext(UserStatus(UserStatus.UserStatusType.Usual, 10))
        }
        semiUsualButton.setOnClickListener {
            viewModel.userStatus.onNext(UserStatus(UserStatus.UserStatusType.SemiUsual, 25))
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
        viewModel.userStatus
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
            }
            .addTo(disposable)

        /*
        viewModel.statusCheckError
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { error ->
                when(error.action) {
                    InView -> {
                        // TODO エラー画面を貼り、リトライする
                        viewModel.doStatusCheck(this)
                    }
                    else -> {
                        showErrorDialog(error) {
                            viewModel.doStatusCheck(this)
                        }
                    }
                }
            }
            .addTo(disposable)*/
    }

    private fun updateBackgroundImage(userStatus: UserStatus) {
        backgroundImageView.setImageResource( when (userStatus.statusType) {
            Usual -> {
                R.drawable.img_home_bg_usual
            }
            SemiUsual -> {
                R.drawable.img_home_bg_semi_usual
            }
        })
    }

    private fun createContentView(userStatus: UserStatus): View {
        return when (userStatus.statusType) {
            Usual,
            SemiUsual -> {
                HomeUsualContentView(this).apply {
                    listener = object : HomeUsualContentView.HomeUsualContentViewEventListener {
                        override fun onClickStayHomeButton() {
                            viewModel.onClickStayHomeButton()
                        }

                        override fun onClickHygieneButton() {
                            viewModel.onClickHygieneButton()
                        }

                        override fun onClickContactButton() {
                            viewModel.onClickContactButton()
                        }

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