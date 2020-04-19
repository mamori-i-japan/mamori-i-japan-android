package jp.co.tracecovid19.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import jp.co.tracecovid19.data.database.deepcontactuser.DeepContactUserDao
import jp.co.tracecovid19.data.database.deepcontactuser.DeepContactUserEntity
import jp.co.tracecovid19.data.database.tempuserid.TempUserIdDao
import jp.co.tracecovid19.data.database.tempuserid.TempUserIdEntity
import jp.co.tracecovid19.data.database.tracedata.TraceDataDao
import jp.co.tracecovid19.data.database.tracedata.TraceDataEntity

@Database(
    entities = [TempUserIdEntity::class, TraceDataEntity::class, DeepContactUserEntity::class],
    version = 1,
    exportSchema = true
)
abstract class TraceCovid19Database : RoomDatabase() {

    abstract fun tempUserIdDao(): TempUserIdDao
    abstract fun traceDataDao(): TraceDataDao
    abstract fun deepContactUserDao(): DeepContactUserDao
}