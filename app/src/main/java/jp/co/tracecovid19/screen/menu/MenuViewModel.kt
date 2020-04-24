package jp.co.tracecovid19.screen.menu

import androidx.lifecycle.ViewModel
import jp.co.tracecovid19.screen.common.LogoutHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


class MenuViewModel(private val logoutHelper: LogoutHelper): ViewModel() {

    lateinit var navigator: MenuNavigator

    fun onClickSetting() {
        navigator.goToSetting()
    }

    fun onClickAbout() {
        navigator.goToAbout()
    }

    fun onClickLicense() {
        navigator.goToLicense()
    }

    // TODO デバッグ用
    fun onClickLogout() {
        runBlocking (Dispatchers.IO) {
            logoutHelper.logout()
        }
        navigator.goToSplash()
    }

    // TODO デバッグ用
    fun onClickRestart() {
        navigator.goToSplash()
    }
}