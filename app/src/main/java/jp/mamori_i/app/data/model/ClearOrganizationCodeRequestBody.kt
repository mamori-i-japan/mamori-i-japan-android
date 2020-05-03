package jp.mamori_i.app.data.model

import com.squareup.moshi.Json

data class ClearOrganizationCodeRequestBody(@Json(name = "randomIDs") val randomIds: List<String>?)