package jp.co.tracecovid19.screen.register

import android.app.Activity
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import jp.co.tracecovid19.data.model.PrefectureType
import jp.co.tracecovid19.data.model.Profile
import jp.co.tracecovid19.data.model.TraceCovid19Error
import jp.co.tracecovid19.data.repository.session.SessionRepository
import jp.co.tracecovid19.data.repository.profile.ProfileRepository


class AuthSmsViewModel(private val sessionRepository: SessionRepository,
                       private val profileRepository: ProfileRepository,
                       private val disposable: CompositeDisposable): ViewModel() {

    lateinit var navigator: AuthSmsNavigator
    val authError = PublishSubject.create<TraceCovid19Error>()

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    fun executeAuth(inputCode: String, verificationId: String, profile: Profile, activity: Activity) {
        navigator.showProgress()
        sessionRepository.authSms(inputCode, verificationId, activity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    navigator.hideProgress()
                    login(profile, activity)
                },
                onError = { e ->
                    navigator.hideProgress()
                    (e as? TraceCovid19Error)?.let { error ->
                        authError.onNext(error)
                    }?: authError.onNext(TraceCovid19Error.unexpectedError())
                }
            ).addTo(disposable)
    }

    private fun login(profile: Profile, activity: Activity) {
        navigator.showProgress()
        sessionRepository.login()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    profileRepository.updateProfile(profile, activity).subscribeBy(
                        onSuccess = {
                            navigator.hideProgress()
                            navigator.goToPermissionSetting()
                        },
                        onError = {
                            navigator.hideProgress()
                            navigator.goToPermissionSetting()
                        }
                    ).addTo(disposable)
                },
                onError = { e ->
                    navigator.hideProgress()
                    (e as? TraceCovid19Error)?.let { error ->
                        authError.onNext(error)
                    }?: authError.onNext(TraceCovid19Error.unexpectedError())
                }
            ).addTo(disposable)
    }
}

