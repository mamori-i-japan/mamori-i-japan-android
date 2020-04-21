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


class InputPhoneNumberViewModel(private val sessionRepository: SessionRepository,
                                private val disposable: CompositeDisposable): ViewModel() {

    lateinit var navigator: InputPhoneNumberNavigator
    val sendError = PublishSubject.create<TraceCovid19Error>()
    val loginError = PublishSubject.create<TraceCovid19Error>()

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    fun onClickSendButton(tel: String, profile: Profile, activity: Activity) {
        navigator.showProgress()
        sessionRepository.authPhoneNumber(tel, activity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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
                    val reason = TraceCovid19Error.mappingReason(e)
                    sendError.onNext(
                        when (reason) {
                            NetWork -> TraceCovid19Error(reason, "文言検討5", DialogCloseOnly)
                            SMSSendLimit -> TraceCovid19Error(reason, "文言検討6", DialogCloseOnly)
                            else -> TraceCovid19Error(reason, "文言検討7", DialogCloseOnly)
                        })
                }
            ).addTo(disposable)
    }

    private fun login(profile: Profile) {
        navigator.showProgress()
        sessionRepository.login(profile.prefectureType(), profile.job)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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
                            NetWork -> TraceCovid19Error(reason, "文言検討12", DialogCloseOnly)
                            else -> TraceCovid19Error(reason, "文言検討13", DialogCloseOnly)
                        })
                }
            ).addTo(disposable)
    }
}