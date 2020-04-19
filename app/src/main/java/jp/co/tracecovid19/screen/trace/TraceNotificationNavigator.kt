package jp.co.tracecovid19.screen.trace

import jp.co.tracecovid19.screen.common.WebTransitionEntity

interface TraceNotificationNavigator {
    fun goToWeb(transitionEntity: WebTransitionEntity)
}