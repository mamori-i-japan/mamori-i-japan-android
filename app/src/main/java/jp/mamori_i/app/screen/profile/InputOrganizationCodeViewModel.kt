package jp.mamori_i.app.screen.profile

import android.app.Activity
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import jp.mamori_i.app.data.model.Profile
import jp.mamori_i.app.data.repository.profile.ProfileRepository
import jp.mamori_i.app.screen.common.LogoutHelper
import jp.mamori_i.app.screen.common.MIJError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class InputOrganizationCodeViewModel(private val profileRepository: ProfileRepository,
                                     private val logoutHelper: LogoutHelper,
                                     private val disposable: CompositeDisposable): ViewModel() {

    lateinit var navigator: InputOrganizationCodeNavigator
    val updateError = PublishSubject.create<MIJError>()

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

    fun onClickUpdateButton(inputOrganizationCode: String) {
        navigator.showProgress()
        profileRepository.updateOrganizationCode(inputOrganizationCode)
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = {
                    navigator.hideProgress()
                    navigator.finishWithCompleteMessage("完了") // TODO
                },
                onError = { e ->
                    navigator.hideProgress()
                    val reason = MIJError.mappingReason(e)
                    if (reason == MIJError.Reason.Auth) {
                        // 認証エラーの場合はログアウト処理をする
                        runBlocking (Dispatchers.IO) {
                            logoutHelper.logout()
                        }
                    }
                    updateError.onNext(
                        when (reason) {
                            MIJError.Reason.NetWork -> MIJError(reason, "文言検討20",
                                MIJError.Action.DialogBack
                            )
                            MIJError.Reason.Auth -> MIJError(reason, "文言検討22",
                                MIJError.Action.DialogLogout
                            )
                            MIJError.Reason.Parse -> MIJError(reason, "文言検討21",
                                MIJError.Action.DialogBack
                            )
                            else -> MIJError(reason, "文言検討21", MIJError.Action.DialogBack)
                        })
                }
            ).addTo(disposable)
    }
}