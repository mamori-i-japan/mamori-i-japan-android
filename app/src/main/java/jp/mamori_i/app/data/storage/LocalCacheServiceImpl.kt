package jp.mamori_i.app.data.storage

import jp.mamori_i.app.data.model.AndroidAppStatus
import jp.mamori_i.app.data.model.PositivePerson

class LocalCacheServiceImpl(override var positivePersonListGeneration: String? = null,
                            override var positivePersonList: List<PositivePerson> = listOf(),
                            override var appStatusGeneration: String? = null,
                            override var appStatus: AndroidAppStatus = AndroidAppStatus(false, "0.0.0", "")) : LocalCacheService