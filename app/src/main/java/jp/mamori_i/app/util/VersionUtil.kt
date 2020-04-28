package jp.mamori_i.app.util

class VersionUtil {

    companion object {
        fun versionCheck(current: String, compareMin: String): Boolean {
            return current >= compareMin
        }
    }

}