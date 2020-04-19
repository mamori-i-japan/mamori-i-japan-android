package jp.co.tracecovid19.data.storage

import jp.co.tracecovid19.data.model.AndroidAppStatus
import jp.co.tracecovid19.data.model.PositivePerson


interface LocalCacheService {

    var positivePersonListGeneration: String?
    var positivePersonList: List<PositivePerson>

    var appStatusGeneration: String?
    var appStatus: AndroidAppStatus
}