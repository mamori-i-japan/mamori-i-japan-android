package jp.mamori_i.app.screen.start

import android.net.Uri

interface AgreementNavigator {
    fun goToInputPrefecture()
    fun openWebBrowser(uri: Uri)
}