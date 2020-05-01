package jp.mamori_i.app.screen.start

import android.net.Uri

interface AgreementNavigator {
    fun showProgress()
    fun hideProgress()
    fun goToPermissionSetting()
    fun openWebBrowser(uri: Uri)
}