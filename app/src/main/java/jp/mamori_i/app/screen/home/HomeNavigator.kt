package jp.mamori_i.app.screen.home

import android.net.Uri

interface HomeNavigator {
    fun showProgress()
    fun hideProgress()
    fun goToMenu()
    fun goToNotification()
    fun goToTraceHistory()
    fun openWebBrowser(uri: Uri)
    fun openShareComponent(title: String, content: String)
}