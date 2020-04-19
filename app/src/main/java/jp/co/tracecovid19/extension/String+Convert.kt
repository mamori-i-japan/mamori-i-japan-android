package jp.co.tracecovid19.extension

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

fun String.convertToUnixTime(stringFormat: String): Long {
    try {
        SimpleDateFormat(stringFormat, Locale.JAPAN).parse(this)?.let {
            return it.time
        }
        return 0 // TODO 0でいいのかこれ
    } catch (e: Exception) {
        return 0
    }
}