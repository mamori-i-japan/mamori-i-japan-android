package jp.mamori_i.app.screen.menu

import jp.mamori_i.app.screen.profile.InputPrefectureTransitionEntity

interface SettingNavigator {
    fun showProgress()
    fun hideProgress()
    fun goToInputPrefecture(transitionEntity: InputPrefectureTransitionEntity)
}