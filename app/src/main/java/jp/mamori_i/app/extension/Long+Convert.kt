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

fun Long.yesterdayPeriod(): Pair<Long, Long> {
    val ymd = this.convertToDateTimeString("yyyyMMdd")
    val to = (ymd + "000000").convertToUnixTime("yyyyMMddHHmmss")
    val from = (ymd + "000000").convertToUnixTime("yyyyMMddHHmmss") - 24 * 60 * 60 * 1000
    return from to to
}

fun Long.twoWeeks(): Long {
    val ymd = this.convertToDateTimeString("yyyyMMdd")
    return (ymd + "000000").convertToUnixTime("yyyyMMddHHmmss") - 14 * 24 * 60 * 60 * 1000
}