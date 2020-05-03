package jp.mamori_i.app.screen.menu

import jp.mamori_i.app.screen.profile.InputPrefectureTransitionEntity
import jp.mamori_i.app.screen.profile.InputOrganizationCodeTransitionEntity

interface SettingNavigator {
    fun showProgress()
    fun hideProgress()
    fun goToInputPrefecture(transitionEntity: InputPrefectureTransitionEntity)
    fun goToInputOrganizationCode(transitionEntity: InputOrganizationCodeTransitionEntity)
    fun clearFinishWithCompleteMessage(message: String)
}