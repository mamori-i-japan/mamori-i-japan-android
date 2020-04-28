package jp.mamori_i.app.bluetooth

import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.GsonBuilder

@Keep
class BLEReadPayload(
    val i: String
) {
    fun getPayload(): ByteArray {
        return gson.toJson(this).toByteArray(Charsets.UTF_8)
    }

    companion object {
        val gson: Gson = GsonBuilder()
            .disableHtmlEscaping().create()

        fun fromPayload(dataBytes: ByteArray): BLEReadPayload {
            val dataString = String(dataBytes, Charsets.UTF_8)
            return gson.fromJson(dataString, BLEReadPayload::class.java)
        }
    }
}