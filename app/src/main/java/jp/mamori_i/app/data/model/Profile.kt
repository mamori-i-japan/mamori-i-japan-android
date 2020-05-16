package jp.mamori_i.app.data.model

import java.io.Serializable

data class Profile(var prefecture: Int = PrefectureType.Tokyo.rawValue): Serializable {
    fun prefectureType(): PrefectureType {
        return PrefectureType.create(prefecture)
    }
}