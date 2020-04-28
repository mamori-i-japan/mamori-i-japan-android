package jp.co.tracecovid19.screen.trace

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import jp.co.tracecovid19.data.database.deepcontactuser.DeepContactUserEntity
import jp.co.tracecovid19.data.model.DeepContact
import jp.co.tracecovid19.data.repository.trase.TraceRepository
import jp.co.tracecovid19.screen.common.LogoutHelper
import jp.co.tracecovid19.screen.common.TraceCovid19Error
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext


class TraceDataUploadViewModel(private val traceRepository: TraceRepository,
                               private val logoutHelper: LogoutHelper,
                               private val disposable: CompositeDisposable): ViewModel(), CoroutineScope {

    lateinit var navigator: TraceDataUploadNavigator

    val uploadState = PublishSubject.create<UploadState>()
    val uploadError = PublishSubject.create<TraceCovid19Error>()

    enum class UploadState {
        Ready,
        InProgress,
        Complete
    }

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        job.cancel()
        disposable.dispose()
        super.onCleared()
    }

    fun onClickUpload() {
        launch(Dispatchers.IO) {
            // まず全件を取得
            val deepContacts = traceRepository.selectAllDeepContactUsers().map { DeepContact.create(it) }
            // TODO バリデーション
            if (deepContacts.count() == 0) { return@launch }
            // アップロード開始
            uploadState.onNext(UploadState.InProgress)
            traceRepository.uploadDeepContacts(deepContacts)
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                    onSuccess = {
                        uploadState.onNext(UploadState.Complete)
                    },
                    onError = { e ->
                        uploadState.onNext(UploadState.Ready)
                        val reason = TraceCovid19Error.mappingReason(e)
                        if (reason == TraceCovid19Error.Reason.Auth) {
                            // 認証エラーの場合はログアウト処理をする
                            runBlocking (Dispatchers.IO) {
                                logoutHelper.logout()
                            }
                        }
                        uploadError.onNext(
                            when (reason) {
                                TraceCovid19Error.Reason.NetWork -> TraceCovid19Error(reason, "文言検討18",
                                    TraceCovid19Error.Action.DialogCloseOnly
                                )
                                TraceCovid19Error.Reason.Auth -> TraceCovid19Error(reason, "文言検討22",
                                    TraceCovid19Error.Action.DialogLogout
                                )
                                else -> TraceCovid19Error(reason, "文言検討19",
                                    TraceCovid19Error.Action.DialogCloseOnly
                                )
                            })
                    }
                ).addTo(disposable)
        }
    }

    fun onClickHome() {
        navigator.goToHome()
    }
}