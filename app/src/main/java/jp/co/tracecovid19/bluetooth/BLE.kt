package jp.co.tracecovid19.bluetooth

import jp.co.tracecovid19.BuildConfig
import java.util.*

object BLE {
    fun supportsCharUUID(charUUID: UUID?): Boolean {
        if (charUUID == null) {
            return false
        }
        return (UUID.fromString(BuildConfig.V2_CHARACTERISTIC_ID) == charUUID)
    }
}