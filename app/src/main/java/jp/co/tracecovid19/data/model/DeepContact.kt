package jp.co.tracecovid19.data.model

import com.squareup.moshi.Json
import jp.co.tracecovid19.data.database.deepcontactuser.DeepContactUserEntity
import jp.co.tracecovid19.extension.convertSHA256HashString

data class DeepContact(val uniqueInsertKey: String,
                       @Json(name = "externalTempId") val tempId: String,
                       @Json(name = "contactStartTime") val startTime: Long,
                       @Json(name = "contactEndTime") val endTime: Long) {

    companion object {
        fun create(entity: DeepContactUserEntity): DeepContact {
            return DeepContact(
                (entity.tempId + entity.startTime.toString() + entity.endTime.toString()).convertSHA256HashString(),
                entity.tempId,
                entity.startTime,
                entity.endTime)
        }
    }
}