package jp.co.tracecovid19.data.storage

import jp.co.tracecovid19.data.storage.LocalStorageService.*

class LocalStorageServiceImpl(private val traceCovid19SharedPreference: TraceCovid19SharedPreference): LocalStorageService {

    /* == Boolean ==*/
    override fun loadBoolean(key: BooleanKey, default: Boolean): Boolean {
        try {
            return traceCovid19SharedPreference.read(key.rawValue, default)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun saveBoolean(key: BooleanKey, value: Boolean) {
        try {
            traceCovid19SharedPreference.save(key.rawValue, value)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun clearBoolean(key: BooleanKey) {
        try {
            traceCovid19SharedPreference.delete(key.rawValue)
        } catch (e: Exception) {
            throw e
        }
    }

    /* == String ==*/
    override fun loadString(key: StringKey, default: String): String {
        try {
            return traceCovid19SharedPreference.read(key.rawValue, default)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun saveString(key: StringKey, value: String) {
        try {
            traceCovid19SharedPreference.save(key.rawValue, value)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun clearString(key: StringKey) {
        try {
            traceCovid19SharedPreference.delete(key.rawValue)
        } catch (e: Exception) {
            throw e
        }
    }

    /* == Int ==*/
    override fun loadInt(key: IntKey, default: Int): Int {
        try {
            return traceCovid19SharedPreference.read(key.rawValue, default)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun saveInt(key: IntKey, value: Int) {
        try {
            traceCovid19SharedPreference.save(key.rawValue, value)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun clearInt(key: IntKey) {
        try {
            traceCovid19SharedPreference.delete(key.rawValue)
        } catch (e: Exception) {
            throw e
        }
    }
}