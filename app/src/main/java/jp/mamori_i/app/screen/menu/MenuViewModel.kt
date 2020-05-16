package jp.mamori_i.app.screen.menu

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import jp.mamori_i.app.screen.common.LogoutHelper
import jp.mamori_i.app.screen.common.MIJError
import jp.mamori_i.app.screen.menu.MenuListItemView.MenuListItem
import jp.mamori_i.app.screen.menu.MenuListItemView.MenuListItemType.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


class MenuViewModel(private val logoutHelper: LogoutHelper,
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

    private fun createMenuItems(): List<MenuListItem> {
        val items = mutableListOf<MenuListItem>()

        items.add(MenuListItem(Setting, "設定") {
            navigator.goToSetting()
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