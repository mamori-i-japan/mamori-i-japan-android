package jp.mamori_i.app.screen.start

import android.net.Uri

interface AgreementNavigator {
    enum class AgreementPageType {
        Agreement1,
        Agreement2
    }

    fun goToNext(pageType: AgreementPageType)
    fun openWebBrowser(uri: Uri)
}