package jp.mamori_i.app.data.storage

import jp.mamori_i.app.data.model.AndroidAppStatus


interface LocalCacheService {

    // 陽性者リスト
    var positivePersonListGeneration: String?
    var positivePersonList: List<String>

    // 組織コード別陽性者リスト
    fun setPositivePersonListGeneration(organizationCode: String, generation: String)
    fun positivePersonListGeneration(organizationCode: String): String?
    fun setPositivePersonList(organizationCode: String, list: List<String>)
    fun positivePersonList(organizationCode: String): List<String>

    // 陽性者リストのクリア
    fun clearPositivePersonList()

    // アプリステータス
    var appStatusGeneration: String?
    var appStatus: AndroidAppStatus
}