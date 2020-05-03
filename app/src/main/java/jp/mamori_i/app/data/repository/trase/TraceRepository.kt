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
    // 組織コード別陽性者リストの取得
    fun fetchPositivePersons(organizationCode: String, activity: Activity): Single<List<PositivePerson>>

    // TempIdのロード
    suspend fun loadTempIds(): List<TempUserId>
    // TempIdのロード(直近2週間)
    suspend fun loadTempIdsFrom2WeeksAgo(currentTime: Long): List<TempUserId>
    // TempUserIdの取得
    suspend fun getTempUserId(currentTime: Long): TempUserIdEntity
    // 直近2週間のtempIdの情報を取得
    // 例) 2020/05/04 12:01:53だとした場合、2020/04/20 00:00:00となり
    // 2週間前の日付の00:00:00からの対象を返却する
    suspend fun getTempUserIdInTwoWeeks(): List<TempUserIdEntity>
    // 2週間前よりも古いTempIdを削除
    suspend fun deleteTempIdInTwoWeeks()

    // TempUserIdのアップロード
    fun uploadTempUserId(tempUserIds: List<TempUserId>, currentTime: Long): Single<Boolean>

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
    // 昨日の濃厚接触者数の取得
    suspend fun countDeepContactUsersAtYesterday(): Int

    // 濃厚接触者の取得
    suspend fun selectDeepContactUsers(ids: List<String>): List<DeepContactUserEntity>

    // 濃厚接触者情報を追加し、該当になった濃厚接触者のデータを接触情報から削除
    suspend fun insertDeepContactUsers(entities: List<DeepContactUserEntity>, tempId: String)

    // 全テータ削除
    suspend fun deleteAllData()
}