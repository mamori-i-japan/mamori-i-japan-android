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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


class SettingViewModel(private val profileRepository: ProfileRepository,
                       private val logoutHelper: LogoutHelper,
                       private val disposable: CompositeDisposable): ViewModel() {

    lateinit var navigator: SettingNavigator
    val profile = PublishSubject.create<Profile>()
    val error = PublishSubject.create<MIJError>()

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
                    error.onNext(
                        when (reason) {
                            NetWork ->
                                MIJError(
                                    reason,
                                    "設定の取得に失敗しました",
                                    "インターネットに接続されていません。\n通信状況の良い環境で再度お試しください。",
                                    DialogBack
                                )
                            Auth ->
                                MIJError(
                                    reason,
                                    "認証エラーが発生しました",
                                    "時間を置いてから再度お試しください。",
                                    DialogLogout
                                ) {
                                    // 認証エラーの場合はログアウト処理をする
                                    runBlocking(Dispatchers.IO) {
                                        logoutHelper.logout()
                                    }
                                }
                            else ->
                                MIJError(
                                    reason,
                                    "不明なエラーが発生しました",
                                    "",
                                    DialogBack
                                )
                        }
                    )
                }
            ).addTo(disposable)
    }

    fun onClickPrefecture() {
        _profile?.let {
            navigator.goToInputPrefecture(InputPrefectureTransitionEntity(it.prefectureType()))
        }
    }
}