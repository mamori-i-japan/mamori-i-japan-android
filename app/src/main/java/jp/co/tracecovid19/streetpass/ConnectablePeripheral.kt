package jp.co.tracecovid19.streetpass

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
class ConnectablePeripheral(
    var manuData: String,
    var transmissionPower: Int?,
    var rssi: Int
) : Parcelable

@Keep
@Parcelize
data class ConnectionRecord(
    val id: String,
    val rssi: Int,
    val txPower: Int?
): Parcelable {
    override fun toString(): String {
        return "id: $id, rssi: $rssi, txPower: $txPower"
    }
}

