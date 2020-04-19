package jp.co.tracecovid19.screen.profile

import jp.co.tracecovid19.screen.register.InputPhoneNumberTransitionEntity

interface InputJobNavigator {
    fun showProgress()
    fun hideProgress()
    fun goToInputPhoneNumber(transitionEntity: InputPhoneNumberTransitionEntity)
    fun finishWithCompleteMessage(message: String)
}