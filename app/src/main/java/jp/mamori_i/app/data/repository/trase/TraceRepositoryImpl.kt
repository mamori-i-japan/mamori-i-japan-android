package jp.mamori_i.app.data.repository.trase

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.room.withTransaction
import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.Moshi
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.mamori_i.app.data.api.trace.TraceApiService
import jp.mamori_i.app.data.database.MIJDatabase
import jp.mamori_i.app.data.database.deepcontactuser.DeepContactUserEntity
import jp.mamori_i.app.data.database.tempuserid.TempUserIdEntity
import jp.mamori_i.app.data.database.tracedata.TraceDataEntity
import jp.mamori_i.app.data.exception.MIJException
import jp.mamori_i.app.data.exception.MIJException.Reason.Auth
import jp.mamori_i.app.data.exception.MIJException.Reason.Parse
import jp.mamori_i.app.data.model.*
import jp.mamori_i.app.data.storage.FirebaseStorageService
import jp.mamori_i.app.data.storage.FirebaseStorageService.FileNameKey.PositivePersonList
import jp.mamori_i.app.data.storage.LocalCacheService
import jp.mamori_i.app.data.storage.LocalStorageService
import jp.mamori_i.app.extension.convertSHA256HashString
import jp.mamori_i.app.extension.convertToDateTimeString
import jp.mamori_i.app.extension.convertToUnixTime
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import java.util.zip.GZIPInputStream


class TraceRepositoryImpl (private val moshi: Moshi,
                           private val api: TraceApiService,
                           private val auth: FirebaseAuth,
                           private val localCacheService: LocalCacheService,
                           private val localStorageService: LocalStorageService,
                           private val firebaseStorageService: FirebaseStorageService,
                           private val db: MIJDatabase): TraceRepository {

    companion object {
        private const val TEMP_ID_SPLIT_TIME = "040000"
    }

    override fun fetchPositivePersons(activity: Activity): Single<List<PositivePerson>> {
        return Single.create { result ->
            // データを取得
            firebaseStorageService.loadDataIfNeeded(PositivePersonList, null, localCacheService.positivePersonListGeneration?:"0", activity)
                .subscribeOn(Schedulers.io())
                .subscribeBy (
                    onSuccess = { data ->
                        // データの取得に成功
                        data.data?.let { listData ->
                            // 取得データあり = 更新する
                            val dataStr = GZIPInputStream(listData.inputStream()).bufferedReader(UTF_8).use { it.readText() }
                            try {
                                // パースする
                                moshi.adapter(PositivePersons::class.java).fromJson(dataStr)?.let { parseResult ->
                                    // パース成功
                                    localCacheService.positivePersonList = parseResult.data
                                    localCacheService.positivePersonListGeneration = data.generation
                                    result.onSuccess(parseResult.data)
                                }?: result.onError( MIJException(Parse)) // データなしもパース失敗扱いとする
                            } catch (e: Throwable) {
                                // パース失敗
                                result.onError(e)
                            }
                        }?: result.onSuccess(localCacheService.positivePersonList) // 取得データなし = キャッシュのやつを使用する
                    },
                    onError = { e ->
                        result.onError(e)
                    }
            )
        }
    }

    override fun fetchPositivePersons(organizationCode: String, activity: Activity): Single<List<PositivePerson>> {
        return Single.create { result ->
            // データを取得
            firebaseStorageService.loadDataIfNeeded(PositivePersonList, organizationCode, localCacheService.positivePersonListGeneration(organizationCode)?:"0", activity)
                .subscribeOn(Schedulers.io())
                .subscribeBy (
                    onSuccess = { data ->
                        // データの取得に成功
                        data.data?.let { listData ->
                            // 取得データあり = 更新する
                            val dataStr = GZIPInputStream(listData.inputStream()).bufferedReader(UTF_8).use { it.readText() }
                            try {
                                // パースする
                                moshi.adapter(PositivePersons::class.java).fromJson(dataStr)?.let { parseResult ->
                                    // パース成功
                                    localCacheService.setPositivePersonList(organizationCode, parseResult.data)
                                    localCacheService.setPositivePersonListGeneration(organizationCode, data.generation)
                                    result.onSuccess(parseResult.data)
                                }?: result.onError( MIJException(Parse)) // データなしもパース失敗扱いとする
                            } catch (e: Throwable) {
                                // パース失敗
                                result.onError(e)
                            }
                        }?: result.onSuccess(localCacheService.positivePersonList(organizationCode)) // 取得データなし = キャッシュのやつを使用する
                    },
                    onError = { e ->
                        result.onError(e)
                    }
                )
        }
    }

    override suspend fun loadTempIds(): List<TempUserId> = db.tempUserIdDao().selectAll().map { TempUserId.create(it) }
    override suspend fun loadTempIdsFrom2WeeksAgo(currentTime: Long): List<TempUserId> = db.tempUserIdDao().selectAll().map { TempUserId.create(it) } // TODO 2週間分とる

    override suspend fun getTempUserId(currentTime: Long): TempUserIdEntity {
        val results = db.tempUserIdDao().getTempUserId(currentTime)
        return if (results.isNotEmpty()) {
            results.first()
        } else {
            val entity = createTempId(currentTime)
            db.tempUserIdDao().insert(entity)
            entity
        }
    }

    override fun uploadTempUserId(tempUserIds: List<TempUserId>, currentTime: Long): Single<Boolean> {
        return Single.create { result ->
            auth.currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.token?.let { token ->
                        val randomId = (UUID.randomUUID().toString() + currentTime.toString()).convertSHA256HashString()
                        val requestBody = UploadTempIdsRequestBody(randomId, tempUserIds)
                        api.uploadTempIds("Bearer $token", requestBody)
                            .subscribeOn(Schedulers.io())
                            .subscribeBy (
                                onSuccess = {
                                    // UploadKeyの追加
                                    localStorageService.addList(LocalStorageService.ListKey.UploadRandomKeys, randomId)
                                    result.onSuccess(true)
                                },
                                onError = { e ->
                                    result.onError(e)
                                }
                            )
                    }?: result.onError(MIJException(Auth))
                } else {
                    result.onError(MIJException(Auth))
                }
            }
        }
    }

    override suspend fun insertTraceData(entity: TraceDataEntity) = db.traceDataDao().insert(entity)
    override fun selectAllTraceData(): LiveData<List<TraceDataEntity>> = db.traceDataDao().selectAll()
    override suspend fun selectTraceTempIdByTempIdGroup(): List<String> = db.traceDataDao().selectTempIdByTempIdGroup()
    override suspend fun selectTraceData(tempId: String): List<TraceDataEntity> = db.traceDataDao().select(tempId)

    override fun selectAllLiveDataDeepContactUsers(): LiveData<List<DeepContactUserEntity>> = db.deepContactUserDao().selectAllLiveData()
    override suspend fun selectAllDeepContactUsers(): List<DeepContactUserEntity> = db.deepContactUserDao().selectAll()
    override suspend fun countDeepContactUsersAtYesterday(): Int {
        // TODO
        return 334
    }

    override suspend fun selectDeepContactUsers(ids: List<String>): List<DeepContactUserEntity> = db.deepContactUserDao().select(ids)

    override suspend fun insertDeepContactUsers(entities: List<DeepContactUserEntity>, tempId: String) {
        db.withTransaction {
            entities.forEach { entity ->
                db.deepContactUserDao().insert(entity)
            }
            db.traceDataDao().delete(tempId)
        }
    }

    private suspend fun saveTempIds(ids: List<TempUserId>): Boolean {
        db.tempUserIdDao().insert(ids.map { TempUserIdEntity(it.tempId, it.startTime, it.expiryTime) })
        return true
    }

    override suspend fun deleteAllData() {
        // TODO 全部のデータを消す
        localCacheService.clearPositivePersonList()
        localStorageService.clearList(LocalStorageService.ListKey.UploadRandomKeys)

        db.tempUserIdDao().deleteAll()
        db.deepContactUserDao().deleteAll()
        db.traceDataDao().deleteAll()
    }

    private fun createTempId(currentTime: Long): TempUserIdEntity {
        var date = currentTime.convertToDateTimeString("yyyyMMdd")
        val zeroTime = (date + "000000").convertToUnixTime("yyyyMMddHHmmss")
        val splitTime = (date + TEMP_ID_SPLIT_TIME).convertToUnixTime("yyyyMMddHHmmss")
        // 00:00:00-04:00:00は前日の日付に補正
        if (currentTime in zeroTime until splitTime) {
            date = (zeroTime - 1 * 1000).convertToDateTimeString("yyyyMMdd")
        }

        val start = (date + TEMP_ID_SPLIT_TIME).convertToUnixTime("yyyyMMddHHmmss")
        val end = start + 24 * 60 * 60 * 1000
        val tempId = UUID.randomUUID().toString() + "$start + $end"
        return TempUserIdEntity(tempId.convertSHA256HashString(), start , end)
    }
}