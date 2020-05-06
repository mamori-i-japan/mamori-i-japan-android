package jp.mamori_i.app.data.model

import com.google.firebase.Timestamp
import jp.mamori_i.app.extension.convertToDateTimeString
import jp.mamori_i.app.extension.convertToTimeInMillis
import java.io.Serializable

data class OrganizationNotice(val messageForAppAccess: String = "",
                              val updatedAt: Timestamp): Serializable {
    fun updatedAtString(): String {
        return updatedAt.seconds.convertToTimeInMillis().convertToDateTimeString("yyyy/MM/dd")
    }

    fun isEmpty(): Boolean {
        return messageForAppAccess.isEmpty()
    }

    companion object {
        fun createEmptyNotice(): OrganizationNotice {
            return OrganizationNotice("", Timestamp.now())
        }
    }
}