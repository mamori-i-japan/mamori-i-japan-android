package jp.mamori_i.app.screen.profile

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import jp.mamori_i.app.data.repository.profile.ProfileRepository
import jp.mamori_i.app.screen.common.LogoutHelper
import jp.mamori_i.app.screen.common.MIJError
import jp.mamori_i.app.screen.common.MIJError.Action.*
import jp.mamori_i.app.screen.common.MIJError.Reason.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class InputOrganizationCodeViewModel(private val profileRepository: ProfileRepository,
                                     private val logoutHelper: LogoutHelper,
                                     private val disposable: CompositeDisposable): ViewModel() {

    lateinit var navigator: InputOrganizationCodeNavigator
    val error = PublishSubject.create<MIJError>()

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
                    error.onNext(
                        when (reason) {
                            Business ->
                                MIJError(
                                    reason,
                                    "組織コードが異なります。",
                                    "",
                                    Inline
                                )
                            NetWork ->
                                MIJError(
                                    reason,
                                    "組織コードの設定に失敗しました",
                                    "インターネットに接続されていません。\n通信状況の良い環境で再度お試しください。",
                                    DialogCloseOnly
                                )
                            Auth ->
                                MIJError(
                                    reason,
                                    "認証エラーが発生しました",
                                    "時間を置いてから再度お試しください。",
                                    DialogLogout
                                ) {
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
                                    DialogCloseOnly
                                )
                        }
                    )
                }
            ).addTo(disposable)
    }
}