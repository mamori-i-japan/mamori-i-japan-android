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
import jp.mamori_i.app.data.model.OrganizationNotice
import jp.mamori_i.app.data.repository.config.ConfigRepository
import jp.mamori_i.app.data.repository.profile.ProfileRepository
import jp.mamori_i.app.data.repository.trase.TraceRepository
import jp.mamori_i.app.screen.common.LogoutHelper
import jp.mamori_i.app.screen.common.MIJError
import jp.mamori_i.app.screen.common.MIJError.Reason.*
import jp.mamori_i.app.screen.common.MIJError.Action.*
import jp.mamori_i.app.screen.home.HomeStatus.HomeStatusType.*
import jp.mamori_i.app.util.AnalysisUtil
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class HomeViewModel(private val traceRepository: TraceRepository,
                    private val configRepository: ConfigRepository,
                    private val profileRepository: ProfileRepository,
                    private val logoutHelper: LogoutHelper,
                    private val disposable: CompositeDisposable): ViewModel(), CoroutineScope {

    companion object {
        // TODO: BuildConfigには出す？
        private const val BORDER_TIME = (3 * 60 * 1000).toLong()
        private const val CONTINUATION_INTERVAL =  (3 * 60 * 1000).toLong()
        private const val DENSITY_INTERVAL =  (5 * 60 * 1000).toLong()
    }

    lateinit var navigator: HomeNavigator
    val homeStatus = PublishSubject.create<HomeStatus>()
    val organizationNotice = PublishSubject.create<OrganizationNotice>()
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
                // ステータスチェック
                doHomeStatusCheck()
                // 組織コード別のお知らせを取得する
                doFetchOrganizationNotice(activity)
            }
            .addTo(disposable)
    }

    fun onClickMenuButton() {
        navigator.goToMenu()
    }

    fun onClickDeepContactCount() {
        navigator.goToTraceHistory()
    }

    fun onClickNotification() {
        navigator.goToNotification()
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

    private fun doHomeStatusCheck() {
        launch(Dispatchers.IO) {
            // まず昨日の濃厚接触数を取得
            when (val count = traceRepository.countDeepContactUsersAtYesterday()) {
                in 0 until 25 -> {
                    homeStatus.onNext(HomeStatus(Usual, count, Date().time))
                }
                else -> {
                    homeStatus.onNext(HomeStatus(SemiUsual, count, Date().time))
                }
            }
        }
    }

    private fun doFetchOrganizationNotice(activity: Activity) {
        // まずプロフィールを取得
        profileRepository.fetchProfile(activity)
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = { profile ->
                    if (profile.organizationCode.isNotEmpty()) {
                        // 組織コード別陽性者リスト取得
                        traceRepository.fetchPositivePersons(profile.organizationCode, activity)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeBy(
                                onSuccess = { positivePersons ->
                                    // DBの濃厚接触リストを取得し、突合させる
                                    launch(Dispatchers.IO) {
                                        val deepContacts = traceRepository.selectAllDeepContactUsers()
                                        AnalysisUtil.analysisDeepContactWithPositivePerson(positivePersons, deepContacts)?.let { _ ->
                                            // 該当者がいればお知らせを取得する
                                            traceRepository.fetchOrganizationNotice(profile.organizationCode, activity)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribeBy(
                                                    onSuccess = {
                                                        organizationNotice.onNext(it)
                                                    },
                                                    onError = { e ->
                                                        handleFetchOrganizationError(e)
                                                    }
                                                ).addTo(disposable)
                                        }?: organizationNotice.onNext(OrganizationNotice.createEmptyNotice()) // 該当者がいない場合はお知らせクリア
                                    }
                                },
                                onError = { e ->
                                    handleFetchOrganizationError(e)
                                }
                            ).addTo(disposable)
                    } else {
                        // 職業コードなしの場合はお知らせクリア
                        organizationNotice.onNext(OrganizationNotice.createEmptyNotice())
                    }
                },
                onError = { e ->
                    handleFetchOrganizationError(e)
                }
            ).addTo(disposable)
    }

    private fun handleFetchOrganizationError(e: Throwable) {
        val reason = MIJError.mappingReason(e)
        if (reason == Auth) {
            // 認証エラーの場合のみ通知し、それ以外は無視する
            error.onNext(MIJError(
                reason,
                "認証エラーが発生しました",
                "時間を置いてから再度お試しください。",
                DialogLogout) {
                runBlocking(Dispatchers.IO) {
                    logoutHelper.logout()
                }
            })
        } else {
            // 認証エラー以外はお知らせをクリアする
            organizationNotice.onNext(OrganizationNotice.createEmptyNotice())
        }
    }
}