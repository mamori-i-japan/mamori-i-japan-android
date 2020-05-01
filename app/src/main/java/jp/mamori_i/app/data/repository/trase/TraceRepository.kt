package jp.mamori_i.app.data.repository.trase

import android.app.Activity
import androidx.lifecycle.LiveData
import io.reactivex.Single
import jp.mamori_i.app.data.database.deepcontactuser.DeepContactUserEntity
import jp.mamori_i.app.data.database.tempuserid.TempUserIdEntity
import jp.mamori_i.app.data.database.tracedata.TraceDataEntity
import jp.mamori_i.app.data.model.DeepContact
import jp.mamori_i.app.data.model.PositivePerson
import jp.mamori_i.app.data.model.TempUserId

interface TraceRepository {
    // 陽性者リストの取得
    fun fetchPositivePersons(activity: Activity): Single<List<PositivePerson>>

    // TempIdのロード
    suspend fun loadTempIds(): List<TempUserId>
    // TempUserIdの取得
    suspend fun getTempUserId(currentTime: Long): TempUserIdEntity

    // 接触者情報の登録
    suspend fun insertTraceData(entity: TraceDataEntity)
    // 接触者情報の全件取得
    fun selectAllTraceData(): LiveData<List<TraceDataEntity>>
    // 接触者情報内の、ユニークなTempIdを取得
    suspend fun selectTraceTempIdByTempIdGroup(): List<String>
    // 指定のtempIdの情報を取得
    suspend fun selectTraceData(tempId: String): List<TraceDataEntity>

    // 濃厚接触者の全件取得(LiveData)
    fun selectAllLiveDataDeepContactUsers(): LiveData<List<DeepContactUserEntity>>
    // 濃厚接触者の全件取得
    suspend fun selectAllDeepContactUsers(): List<DeepContactUserEntity>

    // 濃厚接触者の取得
    suspend fun selectDeepContactUsers(ids: List<String>): List<DeepContactUserEntity>

    // 濃厚接触者情報を追加し、該当になった濃厚接触者のデータを接触情報から削除
    suspend fun insertDeepContactUsers(entities: List<DeepContactUserEntity>, tempId: String)

    // 濃厚接触者情報のアップロード
    fun uploadDeepContacts(contacts: List<DeepContact>): Single<Boolean>

    // 全テータ削除
    suspend fun deleteAllData()
}