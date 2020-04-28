package jp.mamori_i.app.extension

import java.text.SimpleDateFormat
import java.util.*

fun Long.convertToDateTimeString(format: String): String {
    val formatter = SimpleDateFormat(format)
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return formatter.format(calendar.time)
}

fun Long.convertToTimeInMillis(): Long {
    return this * 1000
}

fun Long.convertToUnixTime(): Long {
    return this / 1000
}