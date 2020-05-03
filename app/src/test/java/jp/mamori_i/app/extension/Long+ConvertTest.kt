package jp.mamori_i.app.extension

import org.junit.Assert.*
import org.junit.Test

class LongConvertTest {

    @Test
    fun yesterdayPeriodTest() {
        val format = "yyyyMMddHHmmss"

        var date = "20200504021113".convertToUnixTime(format)
        var actual = date.yesterdayPeriod()
        assertEquals("20200503000000".convertToUnixTime(format), actual.first)
        assertEquals("20200504000000".convertToUnixTime(format), actual.second)

        // 月跨ぎ
        date = "20200501221113".convertToUnixTime(format)
        actual = date.yesterdayPeriod()
        assertEquals("20200430000000".convertToUnixTime(format), actual.first)
        assertEquals("20200501000000".convertToUnixTime(format), actual.second)

        // 年またぎ
        date = "20200101000000".convertToUnixTime(format)
        actual = date.yesterdayPeriod()
        assertEquals("20191231000000".convertToUnixTime(format), actual.first)
        assertEquals("20200101000000".convertToUnixTime(format), actual.second)
    }

    @Test
    fun twoWeekTest() {
        val format = "yyyyMMddHHmmss"

        var date = "20200528141113".convertToUnixTime(format)
        var actual = date.twoWeeks()
        assertEquals("202005140000000".convertToUnixTime(format), actual)

        // 月跨ぎ
        date = "20201012041113".convertToUnixTime(format)
        actual = date.twoWeeks()
        assertEquals("202009280000000".convertToUnixTime(format), actual)

        // 年跨ぎ
        date = "20180103041113".convertToUnixTime(format)
        actual = date.twoWeeks()
        assertEquals("201712200000000".convertToUnixTime(format), actual)
    }
}