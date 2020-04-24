package jp.co.tracecovid19.data.database.deepcontactuser

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DeepContactUserDao {

    @Insert
    suspend fun insert(entity: DeepContactUserEntity)

    @Query("SELECT * FROM deep_contact_user_table")
    fun selectAllLiveData(): LiveData<List<DeepContactUserEntity>>

    @Query("SELECT * FROM deep_contact_user_table")
    suspend fun selectAll(): List<DeepContactUserEntity>

    @Query("SELECT * FROM deep_contact_user_table WHERE tempId IN (:ids) ORDER BY startTime ASC")
    suspend fun select(ids: List<String>): List<DeepContactUserEntity>

    @Query("DELETE FROM deep_contact_user_table")
    suspend fun deleteAll()
}