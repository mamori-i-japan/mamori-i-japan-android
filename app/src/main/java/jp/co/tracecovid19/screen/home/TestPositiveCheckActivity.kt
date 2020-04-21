package jp.co.tracecovid19.screen.home

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
import jp.co.tracecovid19.R
import jp.co.tracecovid19.screen.common.TraceCovid19Error
import jp.co.tracecovid19.data.repository.trase.TraceRepository
import jp.co.tracecovid19.extension.setUpToolBar
import jp.co.tracecovid19.extension.showErrorAlert
import jp.co.tracecovid19.util.AnalysisUtil
import kotlinx.android.synthetic.main.activity_test_positive_check.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext


class TestPositiveCheckActivity: AppCompatActivity(), CoroutineScope {
    companion object {
        const val TAG = "TestPositiveCheckActivity"
        const val KEY = "jp.co.tracecovid19.screen.home.TestPositiveCheckActivity"
    }

    private val repository: TraceRepository by inject()
    private val disposable: CompositeDisposable by inject()

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初期設定
        initialize()
        // viewの初期設定
        setupViews()
        // viewModelとのbind
        bind()
    }

    override fun onResume() {
        super.onResume()
        check()
    }

    private fun initialize() {
        setContentView(R.layout.activity_test_positive_check)
    }

    private fun setupViews() {
        setUpToolBar(toolBar, "陽性者判定(テスト用)")
        refreshButton.setOnClickListener {
            checkResultText.text = ""
            check()
        }

        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        myTempIdListText.setOnLongClickListener {
            clipboardManager?.setPrimaryClip(ClipData.newPlainText("", myTempIdListText.text))
            Toast.makeText(this, "コピーしました", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }

        positiveListText.setOnLongClickListener {
            clipboardManager?.setPrimaryClip(ClipData.newPlainText("", positiveListText.text))
            Toast.makeText(this, "コピーしました", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
    }

    private fun bind() {
    }

    private fun check() {
        repository.fetchPositivePersons(this)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { list ->
                    launch (Dispatchers.IO) {
                        val ids = list.map { it.tempId }
                        val tempIds = repository.loadTempIds()
                        val isPositive = AnalysisUtil.analysisPositive(list, tempIds)

                        withContext(Dispatchers.Main) {
                            myTempIdListText.text = tempIds.map { it.tempId }.joinToString("\n")
                            positiveListText.text = ids.joinToString("\n")
                            if (isPositive) {
                                checkResultText.text = "陽性です"
                            } else {
                                checkResultText.text = "陽性ではありません"
                            }
                        }
                    }
                },
                onError = { error ->
                    showErrorAlert(TraceCovid19Error(TraceCovid19Error.mappingReason(error), "陽性者リスト取得エラー", TraceCovid19Error.Action.DialogCloseOnly))
                }
            ).addTo(disposable)
    }
}