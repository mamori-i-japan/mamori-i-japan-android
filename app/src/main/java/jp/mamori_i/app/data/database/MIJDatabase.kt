package jp.mamori_i.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import jp.mamori_i.app.data.database.deepcontactuser.DeepContactUserDao
import jp.mamori_i.app.data.database.deepcontactuser.DeepContactUserEntity
import jp.mamori_i.app.data.database.tempuserid.TempUserIdDao
import jp.mamori_i.app.data.database.tempuserid.TempUserIdEntity
import jp.mamori_i.app.data.database.tracedata.TraceDataDao
import jp.mamori_i.app.data.database.tracedata.TraceDataEntity

@Database(
    entities = [TempUserIdEntity::class, TraceDataEntity::class, DeepContactUserEntity::class],
    version = 1,
    exportSchema = true
)
abstract class MIJDatabase : RoomDatabase() {

    abstract fun tempUserIdDao(): TempUserIdDao
    abstract fun traceDataDao(): TraceDataDao
    abstract fun deepContactUserDao(): DeepContactUserDao
}