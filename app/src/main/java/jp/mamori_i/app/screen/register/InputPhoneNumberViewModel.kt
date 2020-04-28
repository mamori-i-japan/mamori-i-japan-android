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


class InputPhoneNumberViewModel(private val sessionRepository: SessionRepository,
                                private val disposable: CompositeDisposable): ViewModel() {

    lateinit var navigator: InputPhoneNumberNavigator
    val sendError = PublishSubject.create<MIJError>()
    val loginError = PublishSubject.create<MIJError>()

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    fun onClickSendButton(tel: String, profile: Profile, activity: Activity) {
        navigator.showProgress()
        sessionRepository.authPhoneNumber(tel, activity)
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = { result ->
                    navigator.hideProgress()
                    when {
                        result.didSmsSend -> {
                            // コードが送信された
                            navigator.goToSmsAuth(AuthSmsTransitionEntity(result.verificationId?:"", profile))
                        }
                        else -> {
                            // そのまま認証とおった
                            login(profile)
                        }
                    }
                },
                onError = { e ->
                    navigator.hideProgress()
                    val reason = MIJError.mappingReason(e)
                    sendError.onNext(
                        when (reason) {
                            NetWork -> MIJError(reason, "文言検討5", DialogCloseOnly)
                            SMSSendLimit -> MIJError(reason, "文言検討6", DialogCloseOnly)
                            else -> MIJError(reason, "文言検討7", DialogCloseOnly)
                        })
                }
            ).addTo(disposable)
    }

    private fun login(profile: Profile) {
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
                            NetWork -> MIJError(reason, "文言検討12", DialogCloseOnly)
                            else -> MIJError(reason, "文言検討13", DialogCloseOnly)
                        })
                }
            ).addTo(disposable)
    }
}