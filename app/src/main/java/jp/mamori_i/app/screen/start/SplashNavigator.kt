package jp.mamori_i.app.screen.start

import android.net.Uri

interface SplashNavigator {
    fun goToHome()
    fun goToTutorial()
    fun openWebBrowser(uri: Uri)
}