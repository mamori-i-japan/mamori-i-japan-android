package jp.co.tracecovid19.data.storage


interface TraceCovid19SharedPreference {
    fun <T: Any>read(key: String, default: T): T
    fun <T: Any>save(key: String, value: T)
    fun delete(key: String)
}