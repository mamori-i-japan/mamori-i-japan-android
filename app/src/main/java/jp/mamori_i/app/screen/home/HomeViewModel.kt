package jp.mamori_i.app.screen.home

import android.app.Activity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import jp.mamori_i.app.data.model.AndroidAppStatus
import jp.mamori_i.app.data.repository.config.ConfigRepository
import jp.mamori_i.app.data.repository.trase.TraceRepository
import jp.mamori_i.app.screen.common.LogoutHelper
import jp.mamori_i.app.screen.common.MIJError
import jp.mamori_i.app.screen.common.MIJError.Action.*
import jp.mamori_i.app.screen.common.MIJError.Reason.*
import jp.mamori_i.app.screen.home.HomeStatus.HomeStatusType.*
import jp.mamori_i.app.util.AnalysisUtil
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class HomeViewModel(private val traceRepository: TraceRepository,
                    private val configRepository: ConfigRepository,
                    private val logoutHelper: LogoutHelper,
                    private val disposable: CompositeDisposable): ViewModel(), CoroutineScope {

    companion object {
        // 境界時間(現在時刻から境界時間分を引いた時刻からデータを取得する)
        // この時間内の場合はまだ接触状態が続いている可能性があるので、まだ判定しない
        private const val BORDER_TIME = (3 * 60 * 1000).toLong()
        // 近接状態が継続されていると判定される間隔
        private const val CONTINUATION_INTERVAL =  (3 * 60 * 1000).toLong()
        // 濃厚接触と判定される継続時間(秒)
        private const val DENSITY_INTERVAL =  (15 * 60 * 1000).toLong()
    }

    lateinit var navigator: HomeNavigator
    val homeStatus = PublishSubject.create<HomeStatus>()
    val error = PublishSubject.create<MIJError>()

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        job.cancel()
        disposable.dispose()
        super.onCleared()
    }

    fun onResume(activity: Activity) {
        // アプリステータスチェック
        doAppStatusCheck(activity)
        // 濃厚接触情報の抽出
        doAnalyzeDeepContact()
            .subscribeOn(Schedulers.io())
            .subscribeBy {
                // 陽性者リスト取得
                doFetchPositiveList(activity)
            }
            .addTo(disposable)
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

    fun onClickPositiveReportButton() {
        navigator.goToPositiveReport()
    }

    fun onClickShareButton() {
        // TODO メッセージなど
        navigator.openShareComponent("(TODO)シェアタイトル", "(TODO)シェアメッセージ")
    }

    private fun doAppStatusCheck(activity: Activity) {
        // 設定を取得
        configRepository.fetchAppStatus(activity)
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = { appStatus ->
                    when {
                        appStatus.status() == AndroidAppStatus.Status.Maintenance -> {
                            // メンテナンス
                            error.onNext(MIJError(
                                Maintenance,
                                "ただいまメンテナンス中です",
                                "時間を置いてから再度お試しください。",
                                DialogAppKill)
                            )
                        }
                        appStatus.status() == AndroidAppStatus.Status.ForceUpdate -> {
                            // 強制アップデート
                            error.onNext(MIJError(
                                Version,
                                "最新のバージョンがあります",
                                "ストアより最新のアプリをダウンロードしてください。",
                                DialogAppKill) {
                                    navigator.openWebBrowser(appStatus.storeUrl.toUri())
                            })
                        }
                    }
                },
                onError = {
                    // エラーは無視する
                }
            ).addTo(disposable)
    }

    private fun doAnalyzeDeepContact(): Single<Boolean> {
        return Single.create {
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
                        tempId
                    )
                }

                results.forEach { (result, tempId) ->
                    result?.let {
                        traceRepository.insertDeepContactUsers(it, tempId)
                    }
                }
                it.onSuccess(true)
            }
        }
    }

    private fun doFetchPositiveList(activity: Activity) {
        // まず陽性者リストを取得
        traceRepository.fetchPositivePersons(activity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { positivePersons ->
                    doHomeStatusCheck(positivePersons)
                },
                onError = { e ->
                    val reason = MIJError.mappingReason(e)
                    error.onNext(
                        when (reason) {
                            NetWork ->
                                MIJError(
                                    reason,
                                    "陽性者リストの取得に失敗しました",
                                    "インターネットに接続されていません。\n通信状況の良い環境で再度お試しください。",
                                    DialogRetry
                                ) {
                                    // 再度Fetch
                                    doFetchPositiveList(activity)
                                }
                            Auth ->
                                MIJError(
                                    reason,
                                    "認証エラーが発生しました",
                                    "時間を置いてから再度お試しください。",
                                    DialogLogout
                                ) {
                                    // 認証エラーの場合はログアウト処理をする
                                    runBlocking(Dispatchers.IO) {
                                        logoutHelper.logout()
                                    }
                                }
                            else ->
                                MIJError(
                                    reason,
                                    "不明なエラーが発生しました",
                                    "",
                                    DialogRetry
                                ) {
                                    // 再度Fetch
                                    doFetchPositiveList(activity)
                                }
                        }
                    )
                }
            ).addTo(disposable)
    }

    private fun doHomeStatusCheck(positives: List<String>) {
        launch(Dispatchers.IO) {
            // 最初に昨日の濃厚接触数を取得
            val deepContactCountYesterday = traceRepository.countDeepContactUsersAtYesterday()

            // 陽性判定
            val tempIds = traceRepository.loadTempIdsFrom2WeeksAgo(Date().time)
            if (AnalysisUtil.analysisPositive(positives, tempIds)) {
                homeStatus.onNext(HomeStatus(Positive, deepContactCountYesterday, Date().time))
                return@launch
            }

            // 濃厚接触判定
            val deepContacts = traceRepository.selectAllDeepContactUsers()
            AnalysisUtil.analysisDeepContactWithPositivePerson(positives, deepContacts)?.let {
                homeStatus.onNext(HomeStatus(DeepContact, deepContactCountYesterday, Date().time))
                return@launch
            }

            // ここまでくれば正常、濃厚接触回数による場合わけ
            when (deepContactCountYesterday) {
                in 0 until 25 -> {
                    homeStatus.onNext(HomeStatus(Usual, deepContactCountYesterday, Date().time))
                }
                else -> {
                    homeStatus.onNext(HomeStatus(SemiUsual, deepContactCountYesterday, Date().time))
                }
            }
        }
    }
}