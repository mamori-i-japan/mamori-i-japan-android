package jp.co.tracecovid19.data.repository.config

import android.app.Activity
import com.google.gson.JsonParseException
import com.squareup.moshi.Moshi
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import jp.co.tracecovid19.data.model.*
import jp.co.tracecovid19.data.storage.FirebaseStorageService
import jp.co.tracecovid19.data.storage.LocalCacheService

class ConfigRepositoryImpl(private val moshi: Moshi,
                           private val localCacheService: LocalCacheService,
                           private val firebaseStorageService: FirebaseStorageService): ConfigRepository {

    override fun fetchAppStatus(activity: Activity): Single<AndroidAppStatus> {
        return Single.create { result ->
            // データを取得
            firebaseStorageService.loadDataIfNeeded(FirebaseStorageService.FileNameKey.AppStatus, localCacheService.appStatusGeneration?:"0", activity).subscribeBy (
                onSuccess = { data ->
                    // データの取得に成功
                    data.data?.let {
                        // 取得データあり = 更新する
                        val dataStr = it.toString(Charsets.UTF_8)
                        try {
                            // パースする
                            moshi.adapter(AppStatus::class.java).fromJson(dataStr)?.let { parseResult ->
                                // パース成功
                                localCacheService.appStatus = parseResult.android
                                localCacheService.appStatusGeneration = data.generation
                                result.onSuccess(parseResult.android)
                            }?: result.onError(TraceCovid19Error.create(JsonParseException(""))) // パース失敗
                        } catch (e: Throwable) {
                            // パース失敗
                            result.onError(TraceCovid19Error.create(e))
                        }
                    }?: result.onSuccess(localCacheService.appStatus) // 取得データなし = キャッシュのやつを使用する
                },
                onError = { error ->
                    result.onError(error)
                }
            )
        }
    }
}