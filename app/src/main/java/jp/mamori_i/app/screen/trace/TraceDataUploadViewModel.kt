package jp.mamori_i.app.screen.trace

import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import jp.mamori_i.app.data.repository.trase.TraceRepository
import jp.mamori_i.app.screen.common.LogoutHelper
import jp.mamori_i.app.screen.common.MIJError
import jp.mamori_i.app.screen.common.MIJError.Action.*
import jp.mamori_i.app.screen.common.MIJError.Reason.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext


class TraceDataUploadViewModel(private val traceRepository: TraceRepository,
                               private val logoutHelper: LogoutHelper,
                               private val disposable: CompositeDisposable): ViewModel(), CoroutineScope {

    lateinit var navigator: TraceDataUploadNavigator

    val error = PublishSubject.create<MIJError>()

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        job.cancel()
        disposable.dispose()
        super.onCleared()
    }

    fun onClickUpload(inputCode: String) {
        navigator.showProgress()
        launch(Dispatchers.IO) {
            val currentTime = Date().time
            // まずTempIdリストを取得
            val tempIdsFrom2WeeksAgo = traceRepository.loadTempIdsFrom2WeeksAgo(currentTime)
            // アップロード開始
            traceRepository.uploadTempUserId(tempIdsFrom2WeeksAgo, currentTime, inputCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        navigator.hideProgress()
                        navigator.finishWithCompleteMessage("アップロードが完了しました。\nご協力ありがとうございました。")
                    },
                    onError = { e ->
                        navigator.hideProgress()
                        val reason = MIJError.mappingReason(e)
                        error.onNext(
                            when (reason) {
                                NetWork ->
                                    MIJError(
                                        reason,
                                        "データのアップロードに失敗しました",
                                        "インターネットに接続されていません。\n通信状況の良い環境で再度お試しください。",
                                        DialogCloseOnly)
                                Auth ->
                                    MIJError(
                                        reason,
                                        "認証エラーが発生しました",
                                        "時間を置いてから再度お試しください。",
                                        DialogLogout) {
                                        // 認証エラーの場合はログアウト処理をする
                                        runBlocking (Dispatchers.IO) {
                                            logoutHelper.logout()
                                        }
                                    }
                                else ->
                                    MIJError(
                                        reason,
                                        "不明なエラーが発生しました",
                                        "",
                                        DialogCloseOnly)
                            }
                        )
                    }
                ).addTo(disposable)
        }
    }
}