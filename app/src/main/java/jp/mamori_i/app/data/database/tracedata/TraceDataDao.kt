package jp.mamori_i.app.data.database.tracedata

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TraceDataDao {
    @Insert
    suspend fun insert(entity: TraceDataEntity)

    @Query("SELECT * FROM trace_data_table")
    fun selectAll(): LiveData<List<TraceDataEntity>>

    @Query("SELECT tempId FROM trace_data_table GROUP BY tempId ORDER BY timestamp ASC")
    suspend fun selectTempIdByTempIdGroup(): List<String>

    @Query("SELECT * FROM trace_data_table WHERE tempId = :tempId ORDER BY timestamp ASC")
    suspend fun select(tempId: String): List<TraceDataEntity>

    @Query("DELETE FROM trace_data_table")
    suspend fun deleteAll()

    @Query("DELETE FROM trace_data_table WHERE tempId = :tempId")
    suspend fun delete(tempId: String)
}