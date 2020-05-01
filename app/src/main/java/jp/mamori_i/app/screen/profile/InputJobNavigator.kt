package jp.mamori_i.app.screen.profile

interface InputJobNavigator {
    fun showProgress()
    fun hideProgress()
    fun finishWithCompleteMessage(message: String)
}