package jp.mamori_i.app.screen.menu


interface MenuNavigator {
    fun showProgress()
    fun hideProgress()
    fun goToSetting()
    fun goToTraceDataUpload()
    fun goToAbout()
    fun goToLicense()
    fun goToSplash()
    fun showWithdrawalReportConfirmDialog(message: String)
    fun finishWithdrawalReport(message: String)
}