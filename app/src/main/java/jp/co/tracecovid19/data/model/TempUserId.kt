package jp.co.tracecovid19.data.model

import com.squareup.moshi.Json
import jp.co.tracecovid19.data.database.tempuserid.TempUserIdEntity
import jp.co.tracecovid19.extension.convertTimeInMillis
import jp.co.tracecovid19.extension.convertUnixTime

data class TempUserId(@Json(name = "tempID") val tempId: String,
                      private val validFrom: Long,
                      private val validTo: Long) {

    val startTime = validFrom.convertTimeInMillis()
    val expiryTime = validTo.convertTimeInMillis()

    companion object {

        fun create(entity: TempUserIdEntity): TempUserId {
            // BE側はUnixTimeなのでTimeInMillisとの相互変換が必要
            return TempUserId(entity.tempId,
                entity.startTime.convertUnixTime(),
                entity.expiryTime.convertUnixTime())
        }
    }
}