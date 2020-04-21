package jp.co.tracecovid19.screen.home

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import jp.co.tracecovid19.data.model.RiskStatusType
import jp.co.tracecovid19.screen.common.TraceCovid19Error
import jp.co.tracecovid19.screen.common.TraceCovid19Error.Reason.*
import jp.co.tracecovid19.screen.common.TraceCovid19Error.Action.*
import jp.co.tracecovid19.data.repository.trase.TraceRepository
import jp.co.tracecovid19.util.AnalysisUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class HomeViewModel(private val traceRepository: TraceRepository,
                    private val disposable: CompositeDisposable): ViewModel(), CoroutineScope {

    lateinit var navigator: HomeNavigator
    val currentRiskStatus = PublishSubject.create<RiskStatusType>()
    val bleEnabled = PublishSubject.create<Boolean>()
    val fetchTempIdError = PublishSubject.create<TraceCovid19Error>()
    val statusCheckError = PublishSubject.create<TraceCovid19Error>()

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        job.cancel()
        disposable.dispose()
        super.onCleared()
    }

    fun fetchTempIdIfNeeded() {
        launch (Dispatchers.IO) {
            // TempIDがない場合は取得処理
            val tempIdCount = traceRepository.availableTempUserIdCount(System.currentTimeMillis())
            if (tempIdCount == 0) {
                traceRepository.updateTempIds()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onSuccess = {
                            bleEnabled.onNext(true)
                        },
                        onError = { e ->
                            val reason = TraceCovid19Error.mappingReason(e)
                            fetchTempIdError.onNext(
                                when (reason) {
                                    NetWork -> TraceCovid19Error(reason, "", InView)
                                    // TODO Auth -> TraceCovid19Error(reason, "文言検討3", DialogCloseOnly)
                                    Parse -> TraceCovid19Error(reason, "文言検討14", DialogRetry)
                                    else -> TraceCovid19Error(reason, "文言検討14", DialogRetry)
                                })
                        }
                    )
                    .addTo(disposable)
            } else {
                bleEnabled.onNext(true)
            }
        }
    }

    fun doStatusCheck(activity: Activity) {
        // まず陽性者リストを取得する
        traceRepository.fetchPositivePersons(activity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { positivePersonList ->
                    launch (Dispatchers.IO) {
                        // 陽性チェック(危険度高)
                        // DBに保存されている自身のTempIDリストを取得し、陽性判定
                        if (AnalysisUtil.analysisPositive(positivePersonList, traceRepository.loadTempIds())) {
                                currentRiskStatus.onNext(RiskStatusType.High)
                                return@launch
                        }

                        // 濃厚接触チェック(危険度中)
                        // DBに保存されている濃厚接触リストを取得し、陽性者との濃厚接触判定
                        val deepContacts = traceRepository.selectDeepContactUsers(positivePersonList.map { it.tempId })
                        AnalysisUtil.analysisDeepContactWithPositivePerson(positivePersonList, deepContacts)?.let {
                            // TODO 最後の濃厚接触データを使って表示文言を生成し、連携する
                            Log.d("hoge", it.tempId)
                            currentRiskStatus.onNext(RiskStatusType.Middle)
                            return@launch
                        }

                        // ここまで該当なし(危険度小)
                        currentRiskStatus.onNext(RiskStatusType.Low)
                    }
                },

                onError = { e ->
                    val reason = TraceCovid19Error.mappingReason(e)
                    statusCheckError.onNext(
                        when (reason) {
                            NetWork -> TraceCovid19Error(reason, "", InView)
                            // TODO Auth -> TraceCovid19Error(reason, "文言検討3", DialogCloseOnly)
                            Parse -> TraceCovid19Error(reason, "文言検討15", DialogRetry)
                            else -> TraceCovid19Error(reason, "文言検討15", DialogRetry)
                        })
                }
            ).addTo(disposable)
    }
}