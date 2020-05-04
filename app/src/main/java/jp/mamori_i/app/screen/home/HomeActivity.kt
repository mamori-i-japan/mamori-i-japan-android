package jp.mamori_i.app.screen.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.mamori_i.app.R
import jp.mamori_i.app.extension.showErrorDialog
import jp.mamori_i.app.extension.showSimpleMessageAlert
import jp.mamori_i.app.screen.home.HomeStatus.HomeStatusType.*
import jp.mamori_i.app.screen.menu.MenuActivity
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
        // BLE開始 // TODO 場所の見直し
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
        cardView.listener = object : HomeCardView.HomeCardViewEventListener {
            override fun onClickDeepContactCountArea() {
                viewModel.onClickDeepContactCount()
            }
        }

        notificationView.listener = object: HomeNotificationView.HomeNotificationViewEventListener {
            override fun onClickNotificationButton() {
                viewModel.onClickNotification()
            }
        }

        // TODO 画面切り替え実験用
        usualButton.setOnClickListener {
            viewModel.homeStatus.onNext(HomeStatus(Usual, 10, Date().time))
        }
        semiUsualButton.setOnClickListener {
            viewModel.homeStatus.onNext(HomeStatus(SemiUsual, 25, Date().time))
        }
        notifyButton.setOnClickListener {
            if (it.tag == true) {
                viewModel.notification.onNext("")
                it.tag = false
            } else {
                viewModel.notification.onNext("ON")
                it.tag = true
            }
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

        // お知らせ部分
        viewModel.notification
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { notification ->
                if (!notification.isNullOrEmpty()) {
                    notificationView.visibility = View.VISIBLE
                } else {
                    notificationView.visibility = View.GONE
                }
            }
            .addTo(disposable)

        viewModel.error
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { error ->
                showErrorDialog(error)
            }.addTo(disposable)

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

    private fun updateBackgroundImage(homeStatus: HomeStatus) {
        backgroundImageView.setImageResource( when (homeStatus.statusType) {
            Usual -> {
                R.drawable.img_home_bg_usual
            }
            SemiUsual -> {
                R.drawable.img_home_bg_semi_usual
            }
        })
    }

    private fun createContentView(homeStatus: HomeStatus): View {
        return when (homeStatus.statusType) {
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

    override fun goToNotification() {
        Toast.makeText(this, "TODO 遷移先", Toast.LENGTH_LONG).show()
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

    override fun showForceUpdateDialog(message: String, uri: Uri) {
        // この時点でDisposableをクリアしておく
        disposable.clear()
        showSimpleMessageAlert(message) {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            this.startActivity(intent)
            finish()
            moveTaskToBack(true)
        }
    }

    override fun showMaintenanceDialog(message: String) {
        // この時点でDisposableをクリアしておく
        disposable.clear()
        showSimpleMessageAlert(message) {
            finish()
            moveTaskToBack(true)
        }
    }
}