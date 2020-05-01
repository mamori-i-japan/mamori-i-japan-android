package jp.mamori_i.app.screen.home

import android.app.Activity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import jp.mamori_i.app.data.model.UserStatus
import jp.mamori_i.app.screen.common.MIJError
import jp.mamori_i.app.data.repository.trase.TraceRepository
import jp.mamori_i.app.screen.common.LogoutHelper
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class HomeViewModel(private val traceRepository: TraceRepository,
                    private val logoutHelper: LogoutHelper,
                    private val disposable: CompositeDisposable): ViewModel(), CoroutineScope {

    lateinit var navigator: HomeNavigator
    val userStatus = PublishSubject.create<UserStatus>()
    val statusCheckError = PublishSubject.create<MIJError>()

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        job.cancel()
        disposable.dispose()
        super.onCleared()
    }

    fun doAppStatusCheck(activity: Activity) {
        // TODO
    }

    fun doUserStatusCheck(activity: Activity) {
        // TODO
        userStatus.onNext(UserStatus(UserStatus.UserStatusType.Usual, 2))
    }

    fun onClickMenuButton() {
        navigator.goToMenu()
    }

    fun onClickDeepContactCount() {
        navigator.goToTraceHistory()
    }

    fun onClickStayHomeButton() {
        // TODO URL
        navigator.openWebBrowser("https://yahoo.co.jp".toUri())
    }

    fun onClickHygieneButton() {
        // TODO URL
        navigator.openWebBrowser("https://yahoo.co.jp".toUri())
    }

    fun onClickContactButton() {
        // TODO URL
        navigator.openWebBrowser("https://yahoo.co.jp".toUri())
    }

    fun onClickShareButton() {
        // TODO メッセージなど
        navigator.openShareComponent("(TODO)シェアタイトル", "(TODO)シェアメッセージ")
    }

        /*
        traceRepository.fetchPositivePersons(activity)
            .subscribeOn(Schedulers.io())
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
                    val reason = MIJError.mappingReason(e)
                    if (reason == Auth) {
                        // 認証エラーの場合はログアウト処理をする
                        runBlocking (Dispatchers.IO) {
                            logoutHelper.logout()
                        }
                    }
                    statusCheckError.onNext(
                        when (reason) {
                            NetWork -> MIJError(reason, "", InView)
                            Auth -> MIJError(reason, "文言検討22", DialogLogout)
                            Parse -> MIJError(reason, "文言検討15", DialogRetry)
                            else -> MIJError(reason, "文言検討15", DialogRetry)
                        })
                }
            ).addTo(disposable)*/
}