package jp.co.tracecovid19.data.storage

import jp.co.tracecovid19.data.storage.LocalStorageService.*

class LocalStorageServiceImpl(private val sharedPreferenceManager: SharedPreferenceManager): LocalStorageService {

    /* == Boolean ==*/
    override fun loadBoolean(key: BooleanKey, default: Boolean): Boolean {
        try {
            return sharedPreferenceManager.read(key.rawValue, default)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun saveBoolean(key: BooleanKey, value: Boolean) {
        try {
            sharedPreferenceManager.save(key.rawValue, value)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun clearBoolean(key: BooleanKey) {
        try {
            sharedPreferenceManager.delete(key.rawValue)
        } catch (e: Exception) {
            throw e
        }
    }

    /* == String ==*/
    override fun loadString(key: StringKey, default: String): String {
        try {
            return sharedPreferenceManager.read(key.rawValue, default)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun saveString(key: StringKey, value: String) {
        try {
            sharedPreferenceManager.save(key.rawValue, value)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun clearString(key: StringKey) {
        try {
            sharedPreferenceManager.delete(key.rawValue)
        } catch (e: Exception) {
            throw e
        }
    }

    /* == Int ==*/
    override fun loadInt(key: IntKey, default: Int): Int {
        try {
            return sharedPreferenceManager.read(key.rawValue, default)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun saveInt(key: IntKey, value: Int) {
        try {
            sharedPreferenceManager.save(key.rawValue, value)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun clearInt(key: IntKey) {
        try {
            sharedPreferenceManager.delete(key.rawValue)
        } catch (e: Exception) {
            throw e
        }
    }
}