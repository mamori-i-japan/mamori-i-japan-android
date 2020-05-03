package jp.mamori_i.app.screen.profile

interface InputOrganizationCodeNavigator {
    fun showProgress()
    fun hideProgress()
    fun finishWithCompleteMessage(message: String)
}