package jp.mamori_i.app.screen.profile

import android.app.Activity
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import jp.mamori_i.app.data.model.Profile
import jp.mamori_i.app.data.repository.profile.ProfileRepository
import jp.mamori_i.app.screen.common.LogoutHelper
import jp.mamori_i.app.screen.common.MIJError

class InputJobViewModel(private val profileRepository: ProfileRepository,
                        private val logoutHelper: LogoutHelper,
                        private val disposable: CompositeDisposable): ViewModel() {

    lateinit var navigator: InputJobNavigator
    val updateError = PublishSubject.create<MIJError>()

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

    fun onClickExecuteButton(inputWork: String?,
                             profile: Profile,
                             isRegistrationFlow: Boolean,
                             activity: Activity) {
        // TODO
        /*
        profile.job = inputWork?:""
        if (isRegistrationFlow) {
            navigator.goToInputPhoneNumber(InputPhoneNumberTransitionEntity(profile))
        } else {
            navigator.showProgress()
            profileRepository.updateProfile(profile, activity)
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                onSuccess = {
                    navigator.hideProgress()
                    navigator.finishWithCompleteMessage("更新しました。") // TODO メッセージ
                },
                onError = { e ->
                    navigator.hideProgress()
                    val reason = MIJError.mappingReason(e)
                    if (reason == Auth) {
                        // 認証エラーの場合はログアウト処理をする
                        runBlocking (Dispatchers.IO) {
                            logoutHelper.logout()
                        }
                    }
                    updateError.onNext(
                        when (reason) {
                            NetWork -> MIJError(reason, "文言検討3", DialogCloseOnly)
                            Auth -> MIJError(reason, "文言検討22", DialogLogout)
                            else -> MIJError(reason, "文言検討4", DialogCloseOnly)
                        })
                }
            ).addTo(disposable)
        }*/
    }
}