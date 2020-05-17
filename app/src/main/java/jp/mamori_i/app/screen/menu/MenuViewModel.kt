package jp.mamori_i.app.screen.menu

import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import jp.mamori_i.app.data.repository.trase.TraceRepository
import jp.mamori_i.app.screen.common.LogoutHelper
import jp.mamori_i.app.screen.common.MIJError
import jp.mamori_i.app.screen.menu.MenuListItemView.MenuListItem
import jp.mamori_i.app.screen.menu.MenuListItemView.MenuListItemType.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


class MenuViewModel(private val logoutHelper: LogoutHelper,
                    private val traceRepository: TraceRepository,
                    private val disposable: CompositeDisposable): ViewModel() {

    lateinit var navigator: MenuNavigator
    val menuItems = PublishSubject.create<List<MenuListItem>>()
    val error = PublishSubject.create<MIJError>()

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

    fun fetchMenuItems() {
        menuItems.onNext(createMenuItems())
    }

    fun executeWithdrawalReport() {
        navigator.showProgress()
        traceRepository.deleteTempUserId()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    navigator.hideProgress()
                    navigator.finishWithdrawalReport("取り消しました。")
                },
                onError = { e ->
                    navigator.hideProgress()
                    val reason = MIJError.mappingReason(e)
                    error.onNext(
                        when (reason) {
                            MIJError.Reason.NetWork ->
                                MIJError(
                                    reason,
                                    "取り消しに失敗しました",
                                    "インターネットに接続されていません。\n通信状況の良い環境で再度お試しください。",
                                    MIJError.Action.DialogCloseOnly
                                )
                            MIJError.Reason.Auth ->
                                MIJError(
                                    reason,
                                    "認証エラーが発生しました",
                                    "時間を置いてから再度お試しください。",
                                    MIJError.Action.DialogLogout
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
                                    MIJError.Action.DialogCloseOnly
                                )
                        }
                    )
                }
            ).addTo(disposable)
    }

    private fun createMenuItems(): List<MenuListItem> {
        val items = mutableListOf<MenuListItem>()

        items.add(MenuListItem(Setting, "設定") {
            navigator.goToSetting()
        })

        items.add(MenuListItem(WithdrawalReport, "センターへの情報共有の取り消し") {
            navigator.showWithdrawalReportConfirmDialog("センターへの情報共有を取り消しますか？")
        })

        items.add(MenuListItem(About, "このアプリについて") {
            navigator.goToAbout()
        })

        items.add(MenuListItem(License, "利用ライセンス") {
            navigator.goToLicense()
        })

        // TODO Debug
        items.add(MenuListItem(Logout, "(Debug)ログアウト") {
            runBlocking (Dispatchers.IO) {
                logoutHelper.logout()
            }
            navigator.goToSplash()
        })

        // TODO Debug
        items.add(MenuListItem(Restart, "(Debug)再起動") {
            navigator.goToSplash()
        })

        return items
    }
}