package jp.co.tracecovid19.data.model

import com.squareup.moshi.Json
import jp.co.tracecovid19.data.database.deepcontactuser.DeepContactUserEntity
import jp.co.tracecovid19.extension.*

data class DeepContact(val uniqueInsertKey: String,
                       @Json(name = "externalTempId") val tempId: String,
                       private val contactStartTime: Long,
                       private val contactEndTime: Long) {

    val startTime = contactStartTime.convertToTimeInMillis()
    val endTime = contactEndTime.convertToTimeInMillis()

    companion object {
        fun create(entity: DeepContactUserEntity): DeepContact {
            // BE側はUnixTimeなのでTimeInMillisとの相互変換が必要
            return DeepContact(
                (entity.tempId + entity.startTime.toString() + entity.endTime.toString()).convertSHA256HashString(),
                entity.tempId,
                entity.startTime.convertToUnixTime(),
                entity.endTime.convertToUnixTime())
        }
    }
}