package jp.mamori_i.app.screen.trace

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
import jp.mamori_i.app.extension.setUpToolBar
import jp.mamori_i.app.ui.ProgressHUD
import kotlinx.android.synthetic.main.activity_trace_data_upload.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class TraceDataUploadActivity: AppCompatActivity(), TraceDataUploadNavigator {
    companion object {
        const val KEY = "jp.mamori_i.app.screen.trace.TraceDataUploadActivity"
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
        viewModel.error
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { error ->
                handleError(error)
            }
            .addTo(disposable)
    }

    override fun showProgress() {
        ProgressHUD.show(this)
    }

    override fun hideProgress() {
        ProgressHUD.hide()
    }

    override fun finishWithCompleteMessage(message: String) {
        mainDescriptionTextView.text = message
        subDescriptionTextView.visibility = View.VISIBLE
        uploadButton.visibility = View.GONE
    }

}