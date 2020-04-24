package jp.co.tracecovid19.screen.start

import android.app.Activity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.co.tracecovid19.data.model.AndroidAppStatus
import jp.co.tracecovid19.data.repository.config.ConfigRepository
import jp.co.tracecovid19.data.repository.session.SessionRepository
import jp.co.tracecovid19.screen.common.LogoutHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class SplashViewModel(private val sessionRepository: SessionRepository,
                      private val configRepository: ConfigRepository,
                      private val logoutHelper: LogoutHelper,
                      private val disposable: CompositeDisposable): ViewModel() {

    lateinit var navigator: SplashNavigator

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
                            navigator.showMaintenanceDialog("メンテナンス中です") // TODO メッセージ
                            return@subscribeBy
                        }
                        appStatus.status() == AndroidAppStatus.Status.ForceUpdate -> {
                            // 強制アップデート
                            navigator.showForceUpdateDialog("最新のバージョンがあります。", appStatus.storeUrl.toUri()) // TODO メッセージ
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