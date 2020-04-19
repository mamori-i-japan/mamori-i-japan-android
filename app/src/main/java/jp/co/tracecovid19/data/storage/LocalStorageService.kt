package jp.co.tracecovid19.data.storage


interface LocalStorageService {

    enum class BooleanKey(val rawValue: String) {
        SampleBooleanKey("b_sbk")
    }

    enum class StringKey(val rawValue: String) {
        DebugTokenKey("s_ssk")
    }

    enum class IntKey(val rawValue: String) {
        SampleIntKey("i_sik")
    }

    /* Boolean */
    fun loadBoolean(key: BooleanKey, default: Boolean): Boolean
    fun saveBoolean(key: BooleanKey, value: Boolean)
    fun clearBoolean(key: BooleanKey)

    /* String */
    fun loadString(key: StringKey, default: String): String
    fun saveString(key: StringKey, value: String)
    fun clearString(key: StringKey)

    /* Int */
    fun loadInt(key: IntKey, default: Int): Int
    fun saveInt(key: IntKey, value: Int)
    fun clearInt(key: IntKey)

}