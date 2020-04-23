package jp.co.tracecovid19.screen.menu

import jp.co.tracecovid19.screen.profile.InputPrefectureTransitionEntity
import jp.co.tracecovid19.screen.profile.InputJobTransitionEntity

interface SettingNavigator {
    fun goToInputPrefecture(transitionEntity: InputPrefectureTransitionEntity)
    fun goToInputJob(transitionEntity: InputJobTransitionEntity)
}