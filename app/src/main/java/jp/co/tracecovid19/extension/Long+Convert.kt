package jp.co.tracecovid19.extension

import java.text.SimpleDateFormat
import java.util.*

fun Long.convertToDateTimeString(format: String): String {
    // Create a DateFormatter object for displaying date in specified format.
    val formatter = SimpleDateFormat(format)
    // Create a calendar object that will convert the date and time value in milliseconds to date.
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return formatter.format(calendar.time)
}