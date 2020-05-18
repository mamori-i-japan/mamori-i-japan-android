package jp.mamori_i.app.screen.home.debug

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.mamori_i.app.R
import jp.mamori_i.app.extension.setUpToolBar
import jp.mamori_i.app.logger.DebugLogger
import kotlinx.android.synthetic.main.activity_test_ble.*
import kotlinx.android.synthetic.main.activity_test_ble.toolBar
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class TestBLEActivity: AppCompatActivity() {
    companion object {
        const val TAG = "TestBLEActivity"
        const val KEY = "jp.mamori_i.app.screen.home.debug.TestBLEActivity"
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

        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        logText.setOnLongClickListener {
            clipboardManager?.setPrimaryClip(ClipData.newPlainText("", logText.text))
            Toast.makeText(this, "コピーしました", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
    }

    private fun bind() {

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