package jp.mamori_i.app.screen.start

import android.net.Uri


interface TutorialNavigator {
    fun goToInputPrefecture()
    fun openWebBrowser(uri: Uri)
}