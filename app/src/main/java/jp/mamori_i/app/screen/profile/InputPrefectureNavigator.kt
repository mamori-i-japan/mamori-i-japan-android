package jp.mamori_i.app.screen.profile


interface InputPrefectureNavigator {
    fun showProgress()
    fun hideProgress()
    fun goToPermissionSetting()
    fun finishWithCompleteMessage(message: String)
}