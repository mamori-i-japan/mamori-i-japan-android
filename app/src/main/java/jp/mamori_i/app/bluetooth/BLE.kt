package jp.mamori_i.app.bluetooth

import jp.mamori_i.app.BuildConfig
import java.util.*

object BLE {
    fun supportsCharUUID(charUUID: UUID?): Boolean {
        if (charUUID == null) {
            return false
        }
        return (UUID.fromString(BuildConfig.V2_CHARACTERISTIC_ID) == charUUID)
    }
}