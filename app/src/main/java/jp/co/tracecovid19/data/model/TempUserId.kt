package jp.co.tracecovid19.data.model

import com.squareup.moshi.Json
import jp.co.tracecovid19.data.database.tempuserid.TempUserIdEntity
import jp.co.tracecovid19.extension.convertToTimeInMillis
import jp.co.tracecovid19.extension.convertToUnixTime

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