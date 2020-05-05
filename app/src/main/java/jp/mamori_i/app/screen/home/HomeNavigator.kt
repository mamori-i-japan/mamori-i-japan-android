package jp.mamori_i.app.screen.home

import android.net.Uri
import jp.mamori_i.app.data.model.OrganizationNotice

interface HomeNavigator {
    fun showProgress()
    fun hideProgress()
    fun goToMenu()
    fun goToTraceHistory()
    fun openWebBrowser(uri: Uri)
    fun openShareComponent(title: String, content: String)
}