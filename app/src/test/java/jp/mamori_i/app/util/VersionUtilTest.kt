package jp.mamori_i.app.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VersionUtilTest {

    @Test
    fun version_check_test() {
        assertTrue(VersionUtil.versionCheck("1.0.0", "1.0.0"))
        assertTrue(VersionUtil.versionCheck("1.0.1", "1.0.0"))
        assertTrue(VersionUtil.versionCheck("1.1.0", "1.0.0"))
        assertTrue(VersionUtil.versionCheck("1.11.1", "1.11.0"))
        assertTrue(VersionUtil.versionCheck("2.0.0", "1.0.0"))
        assertTrue(VersionUtil.versionCheck("1.0.0", "0.0.1"))
        assertFalse(VersionUtil.versionCheck("1.0.0", "1.0.1"))
        assertFalse(VersionUtil.versionCheck("1.0.0", "1.1.0"))
        assertFalse(VersionUtil.versionCheck("1.0.0", "1.11.0"))
        assertFalse(VersionUtil.versionCheck("1.0.0", "2.0.0"))
        assertFalse(VersionUtil.versionCheck("0.0.1", "1.0.0"))
        assertFalse(VersionUtil.versionCheck("1.0.0", "1.0.1"))
        assertFalse(VersionUtil.versionCheck("1.11.1", "1.11.2"))

        assertFalse(VersionUtil.versionCheck("1.0.0", "hoge"))
        assertTrue(VersionUtil.versionCheck("1.0.0", "1"))
        assertFalse(VersionUtil.versionCheck("1.0.0", "20"))
        assertTrue(VersionUtil.versionCheck("1.0.0", "0"))
    }
}