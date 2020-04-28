package jp.mamori_i.app.data.database.deepcontactuser

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "deep_contact_user_table", primaryKeys = ["tempId", "startTime"])
class DeepContactUserEntity(
    @ColumnInfo(name = "tempId")
    var tempId: String,
    @ColumnInfo(name = "startTime")
    var startTime: Long,
    @ColumnInfo(name = "endTime")
    var endTime: Long
)