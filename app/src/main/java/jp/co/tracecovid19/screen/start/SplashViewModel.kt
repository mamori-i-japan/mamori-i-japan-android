package jp.co.tracecovid19.screen.start

import android.app.Activity
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import jp.co.tracecovid19.BuildConfig
import jp.co.tracecovid19.data.model.TraceCovid19Error
import jp.co.tracecovid19.data.repository.config.ConfigRepository
import jp.co.tracecovid19.data.repository.session.SessionRepository
import jp.co.tracecovid19.screen.common.LogoutHelper
import jp.co.tracecovid19.util.VersionUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class SplashViewModel(private val sessionRepository: SessionRepository,
                      private val configRepository: ConfigRepository,
                      private val logoutHelper: LogoutHelper,
                      private val disposable: CompositeDisposable): ViewModel() {

    lateinit var navigator: SplashNavigator
    val launchError = PublishSubject.create<TraceCovid19Error>()

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    fun launch(activity: Activity) {
        // 設定を取得
        configRepository.fetchAppStatus(activity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { appStatus ->
                    when {
                        // メンテナンスチェック
                        appStatus.isMaintenance -> {
                            launchError.onNext(TraceCovid19Error(TraceCovid19Error.ErrorType.Maintenance, "メンテナンス中"))
                            return@subscribeBy
                        }
                        !VersionUtil.versionCheck(BuildConfig.VERSION_NAME, appStatus.minVersion) -> {
                            launchError.onNext(TraceCovid19Error(TraceCovid19Error.ErrorType.AppVersion, "バージョンエラー"))
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
                onError = { e ->
                    (e as? TraceCovid19Error)?.let { error ->
                        launchError.onNext(error)
                    }?: launchError.onNext(TraceCovid19Error.unexpectedError())
                }
            ).addTo(disposable)
    }
}