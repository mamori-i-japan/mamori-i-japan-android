package jp.co.tracecovid19.screen.start

import android.net.Uri

interface SplashNavigator {
    fun goToHome()
    fun goToTutorial()
    fun showForceUpdateDialog(message: String, uri: Uri)
    fun showMaintenanceDialog(message: String)
}