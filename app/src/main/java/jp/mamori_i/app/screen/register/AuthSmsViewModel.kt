package jp.mamori_i.app.screen.register

import android.app.Activity
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import jp.mamori_i.app.data.model.Profile
import jp.mamori_i.app.screen.common.MIJError
import jp.mamori_i.app.screen.common.MIJError.Reason.*
import jp.mamori_i.app.screen.common.MIJError.Action.*
import jp.mamori_i.app.data.repository.session.SessionRepository


class AuthSmsViewModel(private val sessionRepository: SessionRepository,
                       private val disposable: CompositeDisposable): ViewModel() {

    lateinit var navigator: AuthSmsNavigator
    val authError = PublishSubject.create<MIJError>()
    val loginError = PublishSubject.create<MIJError>()

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
                    val reason = MIJError.mappingReason(e)
                    authError.onNext(
                        when (reason) {
                            NetWork -> MIJError(reason, "文言検討8", DialogCloseOnly)
                            SMSCodeUnmatched -> MIJError(reason, "文言検討9", Inline)
                            SMSCodeExpired -> MIJError(reason, "文言検討10", DialogBack)
                            else -> MIJError(reason, "文言検討11", DialogCloseOnly)
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
                    val reason = MIJError.mappingReason(e)
                    loginError.onNext(
                        when (reason) {
                            NetWork -> MIJError(reason, "文言検討12", DialogRetry)
                            else -> MIJError(reason, "文言検討13", DialogBack)
                        })
                }
            ).addTo(disposable)
    }
}

