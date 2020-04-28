package jp.mamori_i.app.extension

import java.nio.ByteBuffer
import java.util.*

fun UUID.toBytes(): ByteArray {
    val buffer = ByteBuffer.wrap(ByteArray(16))
    buffer.putLong(mostSignificantBits)
    buffer.putLong(leastSignificantBits)
    return buffer.array()
}

fun ByteArray.toUUID(): UUID? {
    val byteBuffer = ByteBuffer.wrap(this)
    val high = byteBuffer.long
    val low = byteBuffer.long
    return UUID(high, low)
}