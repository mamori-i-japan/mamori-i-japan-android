package jp.mamori_i.app.screen.trace

interface TraceDataUploadNavigator {
    fun showProgress()
    fun hideProgress()
    fun finishWithCompleteMessage(message: String)
}