package jp.mamori_i.app.screen.register


interface AuthSmsNavigator {
    fun showProgress()
    fun hideProgress()
    fun goToPermissionSetting()
}