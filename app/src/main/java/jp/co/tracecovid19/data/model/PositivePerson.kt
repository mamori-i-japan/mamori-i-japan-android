package jp.co.tracecovid19.data.model

import com.squareup.moshi.Json

data class PositivePerson(@Json(name = "tempID") val tempId: String)