package jp.co.tracecovid19.data.storage

import jp.co.tracecovid19.data.model.AndroidAppStatus
import jp.co.tracecovid19.data.model.PositivePerson

class LocalCacheServiceImpl(override var positivePersonListGeneration: String? = null,
                            override var positivePersonList: List<PositivePerson> = listOf(),
                            override var appStatusGeneration: String? = null,
                            override var appStatus: AndroidAppStatus = AndroidAppStatus(false, "0.0.0", "")) : LocalCacheService