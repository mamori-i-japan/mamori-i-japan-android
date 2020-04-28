package jp.mamori_i.app.data.model

import jp.mamori_i.app.BuildConfig
import jp.mamori_i.app.util.VersionUtil

data class AndroidAppStatus(val isMaintenance: Boolean,
                            val minVersion: String,
                            val storeUrl: String) {
    enum class Status {
        Maintenance,
        ForceUpdate,
        OK
    }

    fun status(): Status {
        return when {
            isMaintenance -> return Status.Maintenance
            !VersionUtil.versionCheck(BuildConfig.VERSION_NAME, minVersion) -> return Status.ForceUpdate
            else -> Status.OK
        }
    }
}