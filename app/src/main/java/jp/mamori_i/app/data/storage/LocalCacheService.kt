package jp.mamori_i.app.data.storage

import jp.mamori_i.app.data.model.AndroidAppStatus
import jp.mamori_i.app.data.model.PositivePerson


interface LocalCacheService {

    var positivePersonListGeneration: String?
    var positivePersonList: List<PositivePerson>

    var appStatusGeneration: String?
    var appStatus: AndroidAppStatus
}