package jp.mamori_i.app.data.model

import com.squareup.moshi.Json

data class DeleteTempIdsRequestBody(@Json(name = "randomIDs") val randomIds: List<RandomId>?) {

    // リクエスト時にしか使わないので、内部クラスにしている
    data class RandomId(@Json(name = "randomID") val randomId: String)

}