package jp.mamori_i.app.screen.profile

import jp.mamori_i.app.screen.register.InputPhoneNumberTransitionEntity

interface InputJobNavigator {
    fun showProgress()
    fun hideProgress()
    fun goToInputPhoneNumber(transitionEntity: InputPhoneNumberTransitionEntity)
    fun finishWithCompleteMessage(message: String)
}