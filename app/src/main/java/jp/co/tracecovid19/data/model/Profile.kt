package jp.co.tracecovid19.data.model

import java.io.Serializable

data class Profile(var prefecture: Int = PrefectureType.Tokyo.rawValue, // 初期値を入れないといけないので東京に
                   var job: String = ""): Serializable {
    fun prefectureType(): PrefectureType {
        return PrefectureType.create(prefecture)
    }
}