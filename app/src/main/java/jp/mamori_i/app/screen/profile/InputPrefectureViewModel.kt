package jp.mamori_i.app.screen.profile

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import jp.mamori_i.app.data.model.PrefectureType
import jp.mamori_i.app.data.repository.profile.ProfileRepository
import jp.mamori_i.app.data.repository.session.SessionRepository
import jp.mamori_i.app.screen.common.LogoutHelper
import jp.mamori_i.app.screen.common.MIJError
import jp.mamori_i.app.screen.common.MIJError.Action.*
import jp.mamori_i.app.screen.common.MIJError.Reason.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class InputPrefectureViewModel(private val profileRepository: ProfileRepository,
                               private val sessionRepository: SessionRepository,
                               private val logoutHelper: LogoutHelper,
                               private val disposable: CompositeDisposable): ViewModel() {

    lateinit var navigator: InputPrefectureNavigator
    val error = PublishSubject.create<MIJError>()

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

    fun onClickNextButton(inputPrefecture: PrefectureType) {
        navigator.showProgress()
        sessionRepository.login(inputPrefecture)
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = {
                    navigator.hideProgress()
                    navigator.goToPermissionSetting()
                },
                onError = { e ->
                    navigator.hideProgress()
                    val reason = MIJError.mappingReason(e)
                    error.onNext(
                        when (reason) {
                            NetWork ->
                                MIJError(
                                    reason,
                                    "インターネットに接続できません",
                                    "通信状況の良い環境で再度お試しください。",
                                    DialogCloseOnly)
                            else ->
                                MIJError(
                                    reason,
                                    "不明なエラーが発生しました",
                                    "時間を置いてから再度お試しください。",
                                    DialogCloseOnly)
                        }
                    )
                }
            ).addTo(disposable)
    }

    fun onClickUpdateButton(inputPrefecture: PrefectureType) {
        navigator.showProgress()
        profileRepository.updatePrefecture(inputPrefecture)
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = {
                    navigator.hideProgress()
                    navigator.finishWithCompleteMessage("完了") // TODO
                },
                onError = { e ->
                    navigator.hideProgress()
                    val reason = MIJError.mappingReason(e)
                    error.onNext(
                        when (reason) {
                            NetWork ->
                                MIJError(
                                    reason,
                                    "都道府県の設定に失敗しました",
                                    "インターネットに接続されていません。\n通信状況の良い環境で再度お試しください。",
                                    DialogCloseOnly)
                            Auth ->
                                MIJError(
                                    reason,
                                    "認証エラーが発生しました",
                                    "時間を置いてから再度お試しください。",
                                    DialogLogout) {
                                    // 認証エラーの場合はログアウト処理をする
                                    runBlocking (Dispatchers.IO) {
                                        logoutHelper.logout()
                                    }
                                }
                            else ->
                                MIJError(
                                    reason,
                                    "不明なエラーが発生しました",
                                    "",
                                    DialogCloseOnly)
                        }
                    )
                }
            ).addTo(disposable)
    }
}