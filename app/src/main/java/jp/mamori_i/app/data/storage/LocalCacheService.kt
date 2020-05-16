package jp.mamori_i.app.data.storage

import jp.mamori_i.app.data.model.AndroidAppStatus


interface LocalCacheService {

    // 陽性者リスト
    var positivePersonListGeneration: String?
    var positivePersonList: List<String>
    fun clearPositivePersonList()

    // アプリステータス
    var appStatusGeneration: String?
    var appStatus: AndroidAppStatus
}