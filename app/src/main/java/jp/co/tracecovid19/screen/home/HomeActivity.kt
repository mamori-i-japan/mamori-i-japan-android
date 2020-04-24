package jp.co.tracecovid19.screen.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.co.tracecovid19.R
import jp.co.tracecovid19.data.model.RiskStatusType
import jp.co.tracecovid19.extension.showErrorDialog
import jp.co.tracecovid19.screen.common.TraceCovid19Error.Action.InView
import jp.co.tracecovid19.screen.menu.MenuActivity
import jp.co.tracecovid19.screen.trace.TraceDataUploadActivity
import jp.co.tracecovid19.screen.trace.TraceHistoryActivity
import jp.co.tracecovid19.util.BLEUtil
import kotlinx.android.synthetic.main.activity_home.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeActivity: AppCompatActivity(), HomeNavigator {
    companion object {
        const val KEY = "jp.co.tracecovid19.screen.home.HomeActivity"
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
        // TempIdのFetch
        viewModel.fetchTempIdIfNeeded()
    }

    override fun onResume() {
        super.onResume()
        // ステータスチェック開始
        viewModel.doStatusCheck(this)
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
        // TODO 画面切り替え実験用
        lowButton.setOnClickListener {
            viewModel.currentRiskStatus.onNext(RiskStatusType.Low)
        }
        midButton.setOnClickListener {
            viewModel.currentRiskStatus.onNext(RiskStatusType.Middle)
        }
        highButton.setOnClickListener {
            viewModel.currentRiskStatus.onNext(RiskStatusType.High)
        }

        // TODO 開発用ボタン等なので適当に繋いでいる
        debugButton.setOnClickListener {
            val intent = Intent(this, BLEActivity::class.java)
            this.startActivity(intent)
        }
        // TODO ViewModel経由にする
        menuButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            this.startActivity(intent)
        }
    }

    private fun bind() {
        viewModel.currentRiskStatus
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { status ->
                containerView.removeAllViews()
                containerView.addView(createContentView(status))
            }
            .addTo(disposable)

        viewModel.bleEnabled
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                if (it) {
                    // TODO: ここの前に権限等は確認済みなはず
                    BLEUtil.startBluetoothMonitoringService(this)
                }
            }
            .addTo(disposable)

        viewModel.fetchTempIdError
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { error ->
                when(error.action) {
                    InView -> {
                        // TODO エラー画面を貼り、リトライする
                        viewModel.fetchTempIdIfNeeded()
                    }
                    else -> {
                        showErrorDialog(error) {
                            viewModel.fetchTempIdIfNeeded()
                        }
                    }
                }
            }
            .addTo(disposable)

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
            .addTo(disposable)
    }

    private fun createContentView(riskStatus: RiskStatusType): View {
        return when (riskStatus) {
            RiskStatusType.Low -> {
                return HomeLowRiskContentView(this).apply {
                    listener = object : HomeLowRiskContentView.HomeLowRiskContentViewEventListener {
                        override fun onClickShareButton() {
                            // TODO viewModel経由にする
                            openShareComponents()
                        }
                    }
                }
            }
            RiskStatusType.Middle -> {
                return HomeMiddleRiskContentView(this).apply {
                    listener = object : HomeMiddleRiskContentView.HomeMiddleRiskContentViewEventListener {
                        override fun onClickNotification() {
                            // TODO viewModel経由にする
                            goToTraceNotification()
                        }

                        override fun onClickShareButton() {
                            // TODO viewModel経由にする
                            openShareComponents()
                        }
                    }
                }
            }
            RiskStatusType.High ->{
                return HomeHighRiskContentView(this).apply {
                    listener = object : HomeHighRiskContentView.HomeHighRiskContentViewEventListener {
                        override fun onClickDataUpload() {
                            // TODO viewModel経由にする
                            goToTraceDataUpload()
                        }

                        override fun onClickShareButton() {
                            // TODO viewModel経由にする
                            openShareComponents()
                        }
                    }
                }
            }
        }
    }

    private fun goToTraceNotification() {
        val intent = Intent(this, TraceHistoryActivity::class.java)
        this.startActivity(intent)
    }

    private fun goToTraceDataUpload() {
        val intent = Intent(this, TraceDataUploadActivity::class.java)
        this.startActivity(intent)
    }

    private fun openShareComponents() {
        // TODO シェア文言など
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "共有test。文言仮。")
        }
        startActivity(
            Intent.createChooser(
                intent,
                "共有方法の選択テスト。文言仮。"
            )
        )
    }
}