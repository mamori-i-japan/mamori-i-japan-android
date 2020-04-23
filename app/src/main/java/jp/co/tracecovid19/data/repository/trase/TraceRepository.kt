package jp.co.tracecovid19.data.repository.trase

import android.app.Activity
import androidx.lifecycle.LiveData
import io.reactivex.Single
import jp.co.tracecovid19.data.database.deepcontactuser.DeepContactUserEntity
import jp.co.tracecovid19.data.database.tempuserid.TempUserIdEntity
import jp.co.tracecovid19.data.database.tracedata.TraceDataEntity
import jp.co.tracecovid19.data.model.DeepContact
import jp.co.tracecovid19.data.model.PositivePerson
import jp.co.tracecovid19.data.model.TempUserId

interface TraceRepository {
    // 陽性者リストの取得
    fun fetchPositivePersons(activity: Activity): Single<List<PositivePerson>>

    // TempIdの更新
    fun updateTempIds(): Single<Boolean>
    // TempIdのロード
    suspend fun loadTempIds(): List<TempUserId>
    // 有効なTempIdの数を取得
    suspend fun availableTempUserIdCount(currentTime: Long): Int
    // TempUserIdの取得
    suspend fun getTempUserId(currentTime: Long): List<TempUserIdEntity>
    // 期限の最後になるTempUserIdの取得
    suspend fun getLatestTempUserId(): TempUserIdEntity

    // 接触者情報の登録
    suspend fun insertTraceData(entity: TraceDataEntity)
    // 接触者情報の全件取得
    fun selectAllTraceData(): LiveData<List<TraceDataEntity>>
    // 接触者情報内の、ユニークなTempIdを取得
    suspend fun selectTraceTempIdByTempIdGroup(): List<String>
    // 指定のtempIdの情報を取得
    suspend fun selectTraceData(tempId: String): List<TraceDataEntity>

    // 濃厚接触者の全件取得
    fun selectAllDeepContacttUsers(): LiveData<List<DeepContactUserEntity>>
    // 濃厚接触者の取得
    suspend fun selectDeepContactUsers(ids: List<String>): List<DeepContactUserEntity>

    // 濃厚接触者情報を追加し、該当になった濃厚接触者のデータを接触情報から削除
    suspend fun insertDeepContactUsers(entities: List<DeepContactUserEntity>, tempId: String)

    // 濃厚接触者情報のアップロード
    fun uploadDeepContacts(contacts: List<DeepContact>): Single<Boolean>

    // 全テータ削除
    suspend fun deleteAllData()
}