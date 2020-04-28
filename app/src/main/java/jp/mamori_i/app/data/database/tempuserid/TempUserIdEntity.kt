package jp.mamori_i.app.data.database.tempuserid

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "temp_user_id_table")
class TempUserIdEntity(
    @PrimaryKey
    @ColumnInfo(name = "tempId")
    var tempId: String,
    @ColumnInfo(name = "startTime")
    var startTime: Long,
    @ColumnInfo(name = "expiryTime")
    var expiryTime: Long
)