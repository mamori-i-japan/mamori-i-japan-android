package jp.co.tracecovid19.screen.register


interface InputPhoneNumberNavigator {
    fun showProgress()
    fun hideProgress()
    fun goToSmsAuth(transitionEntity: AuthSmsTransitionEntity)
    fun goToPermissionSetting()
}