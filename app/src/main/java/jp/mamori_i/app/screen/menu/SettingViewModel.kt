package jp.mamori_i.app.screen.menu

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
import jp.mamori_i.app.screen.common.MIJError.Reason.*
import jp.mamori_i.app.screen.common.MIJError.Action.*
import jp.mamori_i.app.screen.profile.InputPrefectureTransitionEntity
import jp.mamori_i.app.screen.profile.InputOrganizationCodeTransitionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


class SettingViewModel(private val profileRepository: ProfileRepository,
                       private val logoutHelper: LogoutHelper,
                       private val disposable: CompositeDisposable): ViewModel() {

    lateinit var navigator: SettingNavigator
    val profile = PublishSubject.create<Profile>()
    val fetchError = PublishSubject.create<MIJError>()
    val clearError = PublishSubject.create<MIJError>()

    private var _profile: Profile? = null

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

    fun fetchProfile(activity: Activity) {
        navigator.showProgress()
        profileRepository.fetchProfile(activity)
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = {
                    navigator.hideProgress()
                    _profile = it
                    profile.onNext(it)
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
                    fetchError.onNext(
                        when (reason) {
                            NetWork -> MIJError(reason, "文言検討20", DialogBack)
                            Auth -> MIJError(reason, "文言検討22", DialogLogout)
                            Parse -> MIJError(reason, "文言検討21", DialogBack)
                            else -> MIJError(reason, "文言検討21", DialogBack)
                        })
                }
            ).addTo(disposable)
    }

    fun onClickPrefecture() {
        _profile?.let {
            navigator.goToInputPrefecture(InputPrefectureTransitionEntity(it.prefectureType()))
        }
    }

    fun onClickOrganization() {
        _profile?.let {
            navigator.goToInputOrganizationCode(InputOrganizationCodeTransitionEntity(it.organizationCode))
        }
    }

    fun clearOrganization() {
        navigator.showProgress()
        profileRepository.clearOrganizationCode()
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = {
                    navigator.hideProgress()
                    navigator.clearFinishWithCompleteMessage("完了") // TODO
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
                    clearError.onNext(
                        when (reason) {
                            NetWork -> MIJError(reason, "文言検討20", DialogCloseOnly)
                            Auth -> MIJError(reason, "文言検討22", DialogLogout)
                            Parse -> MIJError(reason, "文言検討21", DialogCloseOnly)
                            else -> MIJError(reason, "文言検討21", DialogCloseOnly)
                        })
                }
            ).addTo(disposable)
    }
}