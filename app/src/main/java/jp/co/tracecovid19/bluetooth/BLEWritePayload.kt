package jp.co.tracecovid19.bluetooth

import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.GsonBuilder

@Keep
class BLEWritePayload(
    val i: String,
    val rs: Int,
    val txPower: Int?
) {
    fun getPayload(): ByteArray {
        return gson.toJson(this).toByteArray(Charsets.UTF_8)
    }

    companion object {
        val gson: Gson = GsonBuilder()
            .disableHtmlEscaping().create()

        fun fromPayload(dataBytes: ByteArray): BLEWritePayload {
            val dataString = String(dataBytes, Charsets.UTF_8)
            return gson.fromJson(dataString, BLEWritePayload::class.java)
        }
    }
}