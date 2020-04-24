package jp.co.tracecovid19.data.model

import com.squareup.moshi.Json
import jp.co.tracecovid19.data.database.deepcontactuser.DeepContactUserEntity
import jp.co.tracecovid19.extension.convertSHA256HashString
import jp.co.tracecovid19.extension.convertTimeInMillis
import jp.co.tracecovid19.extension.convertUnixTime

data class DeepContact(val uniqueInsertKey: String,
                       @Json(name = "externalTempId") val tempId: String,
                       private val contactStartTime: Long,
                       private val contactEndTime: Long) {

    val startTime = contactStartTime.convertTimeInMillis()
    val endTime = contactEndTime.convertTimeInMillis()

    companion object {
        fun create(entity: DeepContactUserEntity): DeepContact {
            return DeepContact(
                (entity.tempId + entity.startTime.toString() + entity.endTime.toString()).convertSHA256HashString(),
                entity.tempId,
                entity.startTime.convertUnixTime(),
                entity.endTime.convertUnixTime())
        }
    }
}