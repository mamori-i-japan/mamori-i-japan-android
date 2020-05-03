package jp.mamori_i.app.data.database.tempuserid

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TempUserIdDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TempUserIdEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entities: List<TempUserIdEntity>)

    @Query("SELECT count(*) FROM temp_user_id_table")
    suspend fun tempUerIdCount(): Int

    @Query("SELECT * FROM temp_user_id_table")
    suspend fun selectAll(): List<TempUserIdEntity>

    @Query("SELECT * FROM temp_user_id_table WHERE :period <= startTime ORDER BY startTime ASC")
    suspend fun getTempUserIdInPeriod(period: Long): List<TempUserIdEntity>

    @Query("SELECT * FROM temp_user_id_table WHERE startTime <= :currentTime AND :currentTime < expiryTime ORDER BY startTime ASC")
    suspend fun getTempUserId(currentTime: Long): List<TempUserIdEntity>

    @Query("SELECT * FROM temp_user_id_table ORDER BY startTime DESC LIMIT 1")
    suspend fun getLatestTempUserId(): TempUserIdEntity

    @Query("SELECT count(*) FROM temp_user_id_table WHERE :currentTime <= expiryTime")
    suspend fun availableTempUserIdCount(currentTime: Long): Int

    @Query("DELETE FROM temp_user_id_table WHERE tempId IN (:ids)")
    suspend fun deleteTempId(ids: List<String>)

    @Query("DELETE FROM temp_user_id_table WHERE startTime < :period")
    suspend fun deleteOldTempId(period: Long)

    @Query("DELETE FROM temp_user_id_table")
    suspend fun deleteAll()
}