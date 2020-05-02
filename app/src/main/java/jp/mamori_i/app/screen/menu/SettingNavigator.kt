package jp.mamori_i.app.screen.menu

import jp.mamori_i.app.screen.profile.InputPrefectureTransitionEntity
import jp.mamori_i.app.screen.profile.InputJobTransitionEntity

interface SettingNavigator {
    fun showProgress()
    fun hideProgress()
    fun goToInputPrefecture(transitionEntity: InputPrefectureTransitionEntity)
    fun goToInputJob(transitionEntity: InputJobTransitionEntity)
    fun clearFinishWithCompleteMessage(message: String)
}