package jp.mamori_i.app.data.model

import com.squareup.moshi.Json
import jp.mamori_i.app.data.database.tempuserid.TempUserIdEntity
import jp.mamori_i.app.extension.convertToTimeInMillis
import jp.mamori_i.app.extension.convertToUnixTime

data class TempUserId(@Json(name = "tempID") val tempId: String,
                      private val validFrom: Long,
                      private val validTo: Long) {

    val startTime = validFrom.convertToTimeInMillis()
    val expiryTime = validTo.convertToTimeInMillis()

    companion object {

        fun create(entity: TempUserIdEntity): TempUserId {
            // BE側はUnixTimeなのでTimeInMillisとの相互変換が必要
            return TempUserId(entity.tempId,
                entity.startTime.convertToUnixTime(),
                entity.expiryTime.convertToUnixTime())
        }
    }
}