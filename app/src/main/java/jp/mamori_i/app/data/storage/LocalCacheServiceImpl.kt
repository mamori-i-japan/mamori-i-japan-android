package jp.mamori_i.app.data.storage

import jp.mamori_i.app.data.model.AndroidAppStatus

class LocalCacheServiceImpl(override var positivePersonListGeneration: String? = null,
                            override var positivePersonList: List<String> = listOf(),
                            override var appStatusGeneration: String? = null,
                            override var appStatus: AndroidAppStatus = AndroidAppStatus(false, "0.0.0", "")) : LocalCacheService {

    override fun clearPositivePersonList() {
        positivePersonList = listOf()
        positivePersonListGeneration = null
    }
}