package jp.co.tracecovid19.screen.trace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import jp.co.tracecovid19.R
import jp.co.tracecovid19.extension.setUpToolBar
import jp.co.tracecovid19.ui.ProgressHUD
import kotlinx.android.synthetic.main.activity_trace_data_upload.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class TraceDataUploadActivity: AppCompatActivity(), TraceDataUploadNavigator {
    companion object {
        const val KEY = "jp.co.tracecovid19.screen.trace.TraceDataUploadActivity"
    }

    private val viewModel: TraceDataUploadViewModel by viewModel()
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
        setContentView(R.layout.activity_trace_data_upload)
        viewModel.navigator = this
    }

    private fun setupViews() {
        // ツールバー
        setUpToolBar(toolBar, "データアップロード")

        uploadButton.setOnClickListener {
            viewModel.onClickUpload()
        }
    }

    private fun bind() {
    }

    override fun showProgress() {
        ProgressHUD.show(this)
    }

    override fun hideProgress() {
        ProgressHUD.hide()
    }

}