package jp.mamori_i.app.data.storage

import jp.mamori_i.app.data.model.AndroidAppStatus
import jp.mamori_i.app.data.model.PositivePerson


interface LocalCacheService {

    // 陽性者リスト
    var positivePersonListGeneration: String?
    var positivePersonList: List<PositivePerson>

    // 組織コード別陽性者リスト
    fun setPositivePersonListGeneration(organizationCode: String, generation: String)
    fun positivePersonListGeneration(organizationCode: String): String?
    fun setPositivePersonList(organizationCode: String, list: List<PositivePerson>)
    fun positivePersonList(organizationCode: String): List<PositivePerson>

    // 陽性者リストのクリア
    fun clearPositivePersonList()

    // アプリステータス
    var appStatusGeneration: String?
    var appStatus: AndroidAppStatus
}