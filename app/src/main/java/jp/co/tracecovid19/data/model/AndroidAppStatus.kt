package jp.co.tracecovid19.data.model

import jp.co.tracecovid19.BuildConfig
import jp.co.tracecovid19.util.VersionUtil

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