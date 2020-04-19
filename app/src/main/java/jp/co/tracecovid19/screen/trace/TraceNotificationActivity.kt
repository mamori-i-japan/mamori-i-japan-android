package jp.co.tracecovid19.screen.trace

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import jp.co.tracecovid19.R
import jp.co.tracecovid19.extension.setUpToolBar
import jp.co.tracecovid19.screen.common.WebActivity
import jp.co.tracecovid19.screen.common.WebTransitionEntity
import kotlinx.android.synthetic.main.activity_trace_notification.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class TraceNotificationActivity: AppCompatActivity(), TraceNotificationNavigator {
    companion object {
        const val KEY = "jp.co.tracecovid19.screen.trace.TraceNotificationActivity"
    }

    private val viewModel: TraceNotificationViewModel by viewModel()
    private val disposable: CompositeDisposable by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初期設定
        initialize()
        // viewの初期設定
        setupViews()
        // viewModelとのbind
        bind()
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

    private fun initialize() {
        setContentView(R.layout.activity_trace_notification)
        viewModel.navigator = this
    }

    private fun setupViews() {
        // ツールバー
        setUpToolBar(toolBar, "通知")

        webButton.setOnClickListener {
            viewModel.onClickWeb()
        }
    }

    private fun bind() {
    }

    override fun goToWeb(transitionEntity: WebTransitionEntity) {
        val intent = Intent(this, WebActivity::class.java)
        intent.putExtra(WebActivity.KEY, transitionEntity)
        this.startActivity(intent)
    }
}