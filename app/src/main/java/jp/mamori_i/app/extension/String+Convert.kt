package jp.mamori_i.app.extension

import java.lang.Exception
import java.security.MessageDigest
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

fun String.convertSHA256HashString(): String {
    return MessageDigest.getInstance("SHA-256")
        .digest(toByteArray())
        .joinToString(separator = "") {
            "%02x".format(it)
        }
}