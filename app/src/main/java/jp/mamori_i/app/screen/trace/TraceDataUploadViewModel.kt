package jp.mamori_i.app.screen.trace

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import jp.mamori_i.app.data.model.DeepContact
import jp.mamori_i.app.data.repository.trase.TraceRepository
import jp.mamori_i.app.screen.common.LogoutHelper
import jp.mamori_i.app.screen.common.MIJError
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class TraceDataUploadViewModel(private val traceRepository: TraceRepository,
                               private val logoutHelper: LogoutHelper,
                               private val disposable: CompositeDisposable): ViewModel(), CoroutineScope {

    lateinit var navigator: TraceDataUploadNavigator

    val uploadState = PublishSubject.create<UploadState>()
    val uploadError = PublishSubject.create<MIJError>()

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
                        val reason = MIJError.mappingReason(e)
                        if (reason == MIJError.Reason.Auth) {
                            // 認証エラーの場合はログアウト処理をする
                            runBlocking (Dispatchers.IO) {
                                logoutHelper.logout()
                            }
                        }
                        uploadError.onNext(
                            when (reason) {
                                MIJError.Reason.NetWork -> MIJError(reason, "文言検討18",
                                    MIJError.Action.DialogCloseOnly
                                )
                                MIJError.Reason.Auth -> MIJError(reason, "文言検討22",
                                    MIJError.Action.DialogLogout
                                )
                                else -> MIJError(reason, "文言検討19",
                                    MIJError.Action.DialogCloseOnly
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