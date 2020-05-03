package jp.mamori_i.app.data.storage

import jp.mamori_i.app.data.model.AndroidAppStatus
import jp.mamori_i.app.data.model.PositivePerson


interface LocalCacheService {

    var positivePersonListGeneration: String?
    var positivePersonList: List<PositivePerson>

    fun setPositivePersonListGeneration(organizationCode: String, generation: String)
    fun positivePersonListGeneration(organizationCode: String): String?
    fun setPositivePersonList(organizationCode: String, list: List<PositivePerson>)
    fun positivePersonList(organizationCode: String): List<PositivePerson>

    var appStatusGeneration: String?
    var appStatus: AndroidAppStatus

}