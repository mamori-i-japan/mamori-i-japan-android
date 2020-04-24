package jp.co.tracecovid19.extension

import java.text.SimpleDateFormat
import java.util.*

fun Long.convertToDateTimeString(format: String): String {
    val formatter = SimpleDateFormat(format)
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return formatter.format(calendar.time)
}

fun Long.convertTimeInMillis(): Long {
    return this * 1000
}

fun Long.convertUnixTime(): Long {
    return this / 1000
}