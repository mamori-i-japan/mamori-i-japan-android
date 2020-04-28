package jp.mamori_i.app.data.database.tracedata

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "trace_data_table", primaryKeys = ["tempId", "timestamp"])
class TraceDataEntity(
    @ColumnInfo(name = "tempId")
    var tempId: String,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    @ColumnInfo(name = "rssi")
    val rssi: Int,
    @ColumnInfo(name = "txPower")
    val txPower: Int?
) {
    override fun toString(): String {
        return "tempId: $tempId, " +
                "timestamp: $timestamp, " +
                "rssi: $rssi, " +
                "txPower: $txPower"
    }
}