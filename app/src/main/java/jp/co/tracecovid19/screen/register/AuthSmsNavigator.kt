package jp.co.tracecovid19.screen.register


interface AuthSmsNavigator {
    fun showProgress()
    fun hideProgress()
    fun goToPermissionSetting()
}