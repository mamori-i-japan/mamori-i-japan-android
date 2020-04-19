package jp.co.tracecovid19.data.model

import com.squareup.moshi.Json
import jp.co.tracecovid19.data.database.tempuserid.TempUserIdEntity
import jp.co.tracecovid19.extension.convertToDateTimeString
import jp.co.tracecovid19.extension.convertToUnixTime

data class TempUserId(@Json(name = "tempID") val tempId: String,
                      val validFrom: String,
                      val validTo: String) {

    val fromTime =  validFrom.convertToUnixTime(format)
    val toTime = validTo.convertToUnixTime(format)

    companion object {
        // TODO せっかくUnixTimeなのにわざわざStringに戻して、使うときにLongに戻るの超微妙
        const val format = "yyyy-MM-dd'T'HH:mm:ss.SSS"

        fun create(entity: TempUserIdEntity): TempUserId {
            return TempUserId(entity.tempId,
                entity.startTime.convertToDateTimeString(format),
                entity.expiryTime.convertToDateTimeString(format))
        }
    }

}