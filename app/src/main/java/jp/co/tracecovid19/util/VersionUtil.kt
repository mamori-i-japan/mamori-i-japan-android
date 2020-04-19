package jp.co.tracecovid19.util

class VersionUtil {

    companion object {
        fun versionCheck(current: String, compareMin: String): Boolean {
            return current >= compareMin
        }
    }

}