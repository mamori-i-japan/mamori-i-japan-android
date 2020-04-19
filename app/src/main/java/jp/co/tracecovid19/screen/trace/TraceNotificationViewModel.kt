package jp.co.tracecovid19.screen.trace

import androidx.lifecycle.ViewModel
import jp.co.tracecovid19.screen.common.WebTransitionEntity
import kotlin.concurrent.thread


class TraceNotificationViewModel: ViewModel() {

    lateinit var navigator: TraceNotificationNavigator

    fun onClickWeb() {
        // TODO 仮である
        navigator.goToWeb(WebTransitionEntity("https://www.mhlw.go.jp/bunya/kenkou/hokenjo/h_13.html"))
    }
}