package jp.mamori_i.app.screen.profile


interface InputPrefectureNavigator {
    fun showProgress()
    fun hideProgress()
    fun goToInputWork(transitionEntity: InputJobTransitionEntity)
    fun finishWithCompleteMessage(message: String)
}