package jp.mamori_i.app.data.model

import java.io.Serializable

data class Profile(var prefecture: Int = PrefectureType.Tokyo.rawValue, // 初期値を入れないといけないので東京に
                   var organizationCode: String = ""): Serializable {
    fun prefectureType(): PrefectureType {
        return PrefectureType.create(prefecture)
    }
}