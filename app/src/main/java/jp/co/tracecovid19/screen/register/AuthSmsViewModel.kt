package jp.co.tracecovid19.screen.register

import android.app.Activity
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import jp.co.tracecovid19.data.model.Profile
import jp.co.tracecovid19.screen.common.TraceCovid19Error
import jp.co.tracecovid19.screen.common.TraceCovid19Error.Reason.*
import jp.co.tracecovid19.screen.common.TraceCovid19Error.Action.*
import jp.co.tracecovid19.data.repository.session.SessionRepository
import jp.co.tracecovid19.data.repository.profile.ProfileRepository


class AuthSmsViewModel(private val sessionRepository: SessionRepository,
                       private val disposable: CompositeDisposable): ViewModel() {

    lateinit var navigator: AuthSmsNavigator
    val authError = PublishSubject.create<TraceCovid19Error>()
    val loginError = PublishSubject.create<TraceCovid19Error>()

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    fun executeAuth(inputCode: String, verificationId: String, profile: Profile, activity: Activity) {
        navigator.showProgress()
        sessionRepository.authSms(inputCode, verificationId, activity)
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = {
                    navigator.hideProgress()
                    executeLogin(profile)
                },
                onError = { e ->
                    navigator.hideProgress()
                    val reason = TraceCovid19Error.mappingReason(e)
                    authError.onNext(
                        when (reason) {
                            NetWork -> TraceCovid19Error(reason, "文言検討8", DialogCloseOnly)
                            SMSCodeUnmatched -> TraceCovid19Error(reason, "文言検討9", Inline)
                            SMSCodeExpired -> TraceCovid19Error(reason, "文言検討10", DialogBack)
                            else -> TraceCovid19Error(reason, "文言検討11", DialogCloseOnly)
                        })
                }
            ).addTo(disposable)
    }

    fun executeLogin(profile: Profile) {
        navigator.showProgress()
        sessionRepository.login(profile.prefectureType(), profile.job)
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = {
                    navigator.hideProgress()
                    navigator.goToPermissionSetting()
                },
                onError = { e ->
                    navigator.hideProgress()
                    val reason = TraceCovid19Error.mappingReason(e)
                    loginError.onNext(
                        when (reason) {
                            NetWork -> TraceCovid19Error(reason, "文言検討12", DialogRetry)
                            else -> TraceCovid19Error(reason, "文言検討13", DialogBack)
                        })
                }
            ).addTo(disposable)
    }
}

