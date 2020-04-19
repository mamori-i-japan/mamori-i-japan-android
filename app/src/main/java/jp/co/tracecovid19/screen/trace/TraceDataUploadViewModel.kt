package jp.co.tracecovid19.screen.trace

import androidx.lifecycle.ViewModel
import jp.co.tracecovid19.screen.common.WebTransitionEntity
import kotlin.concurrent.thread


class TraceDataUploadViewModel: ViewModel() {

    lateinit var navigator: TraceDataUploadNavigator


    fun onClickUpload() {
        navigator.showProgress()
        thread {
            // TODO 通信していないので、とりあえずsleep入れてる
            Thread.sleep(1000L)
            navigator.hideProgress()
        }
    }
}