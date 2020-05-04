package jp.mamori_i.app.screen.start

import android.app.Activity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import jp.mamori_i.app.data.model.AndroidAppStatus
import jp.mamori_i.app.data.repository.config.ConfigRepository
import jp.mamori_i.app.data.repository.session.SessionRepository
import jp.mamori_i.app.screen.common.LogoutHelper
import jp.mamori_i.app.screen.common.MIJError
import jp.mamori_i.app.screen.common.MIJError.Reason.*
import jp.mamori_i.app.screen.common.MIJError.Action.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class SplashViewModel(private val sessionRepository: SessionRepository,
                      private val configRepository: ConfigRepository,
                      private val logoutHelper: LogoutHelper,
                      private val disposable: CompositeDisposable): ViewModel() {

    lateinit var navigator: SplashNavigator
    val error = PublishSubject.create<MIJError>()

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    fun launch(activity: Activity) {
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
                                DialogAppKill
                            ))
                            return@subscribeBy
                        }
                        appStatus.status() == AndroidAppStatus.Status.ForceUpdate -> {
                            // 強制アップデート
                            error.onNext(MIJError(
                                Version,
                                "最新のバージョンがあります",
                                "ストアより最新のアプリをダウンロードしてください。",
                                DialogAppKill
                            ) {
                                navigator.openWebBrowser(appStatus.storeUrl.toUri())
                            })
                            return@subscribeBy
                        }
                        sessionRepository.isLogin() -> {
                            // ホームへ
                            navigator.goToHome()
                        }
                        else -> {
                            // ログアウト処理をしてチュートリアルへ
                            runBlocking (Dispatchers.IO) {
                                logoutHelper.logout()
                            }
                            navigator.goToTutorial()
                        }
                    }
                },
                onError = {
                    // エラーは無視する
                }
            ).addTo(disposable)
    }
}