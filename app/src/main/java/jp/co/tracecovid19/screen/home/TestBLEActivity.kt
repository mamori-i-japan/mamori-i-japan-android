package jp.co.tracecovid19.screen.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.co.tracecovid19.R
import jp.co.tracecovid19.extension.setUpToolBar
import jp.co.tracecovid19.logger.DebugLogger
import kotlinx.android.synthetic.main.activity_test_ble.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class TestBLEActivity: AppCompatActivity() {
    companion object {
        const val TAG = "TestBLEActivity"
        const val KEY = "jp.co.tracecovid19.screen.home.TestBLEActivity"
    }

    private val viewModel: TestBLEViewModel by viewModel()
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

    override fun onStart() {
        super.onStart()
        viewModel.subscribe()
    }

    override fun onStop() {
        super.onStop()
        viewModel.unsubscribe()
    }

    private fun initialize() {
        setContentView(R.layout.activity_test_ble)
    }

    private fun setupViews() {
        setUpToolBar(toolBar, "BLEテスト")
    }

    private fun bind() {
        viewModel.tempId.observe(this, Observer {
            tokenText.text = it
        })

        DebugLogger.logString
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { logString ->
                val str = logText.text?: ""
                logText.text = str.toString() + "\n" + logString
            }
            .addTo(disposable)
    }
}