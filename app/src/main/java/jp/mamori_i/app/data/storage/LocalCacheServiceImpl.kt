package jp.mamori_i.app.data.storage

import jp.mamori_i.app.data.model.AndroidAppStatus
import jp.mamori_i.app.data.model.PositivePerson
import java.util.*

class LocalCacheServiceImpl(override var positivePersonListGeneration: String? = null,
                            override var positivePersonList: List<PositivePerson> = listOf(),
                            override var appStatusGeneration: String? = null,
                            override var appStatus: AndroidAppStatus = AndroidAppStatus(false, "0.0.0", "")) : LocalCacheService {

    // 組織コード別陽性者リスト保持用のMap
    private val positivePersonListGenerationMap: MutableMap<String, String> = mutableMapOf()
    private val positivePersonListMap: MutableMap<String, List<PositivePerson>> = mutableMapOf()

    override fun setPositivePersonListGeneration(organizationCode: String, generation: String) {
        positivePersonListGenerationMap[organizationCode] = generation
    }

    override fun positivePersonListGeneration(organizationCode: String): String? {
        return positivePersonListGenerationMap[organizationCode]
    }

    override fun setPositivePersonList(organizationCode: String, list: List<PositivePerson>) {
        positivePersonListMap[organizationCode] = list
    }

    override fun positivePersonList(organizationCode: String): List<PositivePerson> {
        return positivePersonListMap[organizationCode]?: listOf()
    }

    override fun clearPositivePersonList() {
        positivePersonList = listOf()
        positivePersonListGeneration = null
        positivePersonListMap.clear()
        positivePersonListGenerationMap.clear()
    }
}