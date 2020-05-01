package jp.mamori_i.app.screen.profile

import jp.mamori_i.app.screen.start.AgreementTransitionEntity


interface InputPrefectureNavigator {
    fun showProgress()
    fun hideProgress()
    fun goToAgreement(transitionEntity: AgreementTransitionEntity)
    fun finishWithCompleteMessage(message: String)
}