package jp.co.tracecovid19.screen.profile


interface InputPrefectureNavigator {
    fun showProgress()
    fun hideProgress()
    fun goToInputWork(transitionEntity: InputJobTransitionEntity)
    fun finishWithCompleteMessage(message: String)
}