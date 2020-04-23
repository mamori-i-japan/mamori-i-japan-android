package jp.co.tracecovid19.data.model

import com.squareup.moshi.Json
import jp.co.tracecovid19.data.database.deepcontactuser.DeepContactUserEntity

data class DeepContact(@Json(name = "tempID") val tempId: String,
                       val startTime: Long,
                       val endTime: Long) {
    companion object {
        fun create(entity: DeepContactUserEntity): DeepContact {
            return DeepContact(
                entity.tempId,
                entity.startTime,
                entity.endTime)
        }
    }
}