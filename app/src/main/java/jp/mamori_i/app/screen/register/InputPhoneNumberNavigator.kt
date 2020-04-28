package jp.mamori_i.app.screen.register


interface InputPhoneNumberNavigator {
    fun showProgress()
    fun hideProgress()
    fun goToSmsAuth(transitionEntity: AuthSmsTransitionEntity)
    fun goToPermissionSetting()
}