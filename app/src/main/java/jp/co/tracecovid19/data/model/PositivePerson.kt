package jp.co.tracecovid19.data.model

import com.squareup.moshi.Json

data class PositivePerson(@Json(name = "uuid") val tempId: String)