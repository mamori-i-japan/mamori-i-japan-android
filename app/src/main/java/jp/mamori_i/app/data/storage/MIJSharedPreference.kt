package jp.mamori_i.app.data.storage


interface MIJSharedPreference {
    fun <T: Any>read(key: String, default: T): T
    fun <T: Any>save(key: String, value: T)
    fun delete(key: String)
}