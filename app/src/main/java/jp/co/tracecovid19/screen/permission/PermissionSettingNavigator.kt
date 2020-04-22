package jp.co.tracecovid19.screen.permission


interface PermissionSettingNavigator {
    enum class PermissionSettingPageType {
        BLE
    }

    fun goToNext(pageType: PermissionSettingPageType)
}