package jp.mamori_i.app.screen.home

import android.app.Activity
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import jp.mamori_i.app.data.repository.profile.ProfileRepository
import jp.mamori_i.app.data.repository.trase.TraceRepository
import jp.mamori_i.app.util.AnalysisUtil
import kotlinx.coroutines.launch

class TestContactListViewModel(
    private val traceRepository: TraceRepository,
    private val disposable: CompositeDisposable) : ViewModel() {

    companion object {
        // TODO: BuildConfigには出す？
        private const val BORDER_TIME = (3 * 60 * 1000).toLong()
        private const val CONTINUATION_INTERVAL =  (3 * 60 * 1000).toLong()
        private const val DENSITY_INTERVAL =  (5 * 60 * 1000).toLong()
    }

    val checkResult = PublishSubject.create<TestContactModel>()
    val checkResultNone = PublishSubject.create<Any>()

    val deepContactUserModels = Transformations.map(traceRepository.selectAllLiveDataDeepContactUsers()) {
        it.map { entity -> TestContactModel(entity.tempId, entity.startTime, entity.endTime) }
    }

    fun checkDeepContactWithPositivePerson(activity: Activity) {
        // 陽性者と濃厚接触しているかどうか
        traceRepository.fetchPositivePersons(activity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { list ->
                    deepContactUserModels.value?.firstOrNull { list.contains(it.tempId) }?.let { matched ->
                        checkResult.onNext(matched)
                    }?: checkResultNone.onNext("None")
                },
                onError = { error ->
                    // TODO エラー
                }
            ).addTo(disposable)
    }

    fun analyzeDeepContact() {
        viewModelScope.launch {
            val tempIds = traceRepository.selectTraceTempIdByTempIdGroup()
            val results = tempIds.map { tempId ->
                val targetList = traceRepository.selectTraceData(tempId)
                // NOTE ここでPairにしてTempIDと紐づけておかないと
                // 濃厚接触0件の場合に後にtempIDがわからなくなる
                Pair(
                    AnalysisUtil.analysisDeepContacts(
                        targetList,
                        System.currentTimeMillis() - BORDER_TIME,
                        CONTINUATION_INTERVAL,
                        DENSITY_INTERVAL
                    ),
                    tempId)
            }

            results.forEach { (result, tempId) ->
                result?.let {
                    traceRepository.insertDeepContactUsers(it, tempId)
                }
            }
        }
    }
}

data class TestContactModel(val tempId: String, val startTime: Long, val endTime: Long)