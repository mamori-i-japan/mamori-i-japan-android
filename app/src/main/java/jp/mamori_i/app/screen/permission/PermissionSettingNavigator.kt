package jp.mamori_i.app.screen.permission


interface PermissionSettingNavigator {
    enum class PermissionSettingPageType {
        BLE
    }

    fun goToNext(pageType: PermissionSettingPageType)
}