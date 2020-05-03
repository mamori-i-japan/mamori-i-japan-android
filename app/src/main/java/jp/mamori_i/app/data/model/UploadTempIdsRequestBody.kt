package jp.mamori_i.app.data.model

import com.squareup.moshi.Json

data class UploadTempIdsRequestBody(@Json(name = "randomID") val randomId: String,
                                    @Json(name = "tempIDs")val tempIds: List<TempUserId>)