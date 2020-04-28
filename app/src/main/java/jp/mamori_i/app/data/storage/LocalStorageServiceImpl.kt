package jp.mamori_i.app.data.storage

import jp.mamori_i.app.data.storage.LocalStorageService.*

class LocalStorageServiceImpl(private val MIJSharedPreference: MIJSharedPreference): LocalStorageService {

    /* == Boolean ==*/
    override fun loadBoolean(key: BooleanKey, default: Boolean): Boolean {
        try {
            return MIJSharedPreference.read(key.rawValue, default)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun saveBoolean(key: BooleanKey, value: Boolean) {
        try {
            MIJSharedPreference.save(key.rawValue, value)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun clearBoolean(key: BooleanKey) {
        try {
            MIJSharedPreference.delete(key.rawValue)
        } catch (e: Exception) {
            throw e
        }
    }

    /* == String ==*/
    override fun loadString(key: StringKey, default: String): String {
        try {
            return MIJSharedPreference.read(key.rawValue, default)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun saveString(key: StringKey, value: String) {
        try {
            MIJSharedPreference.save(key.rawValue, value)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun clearString(key: StringKey) {
        try {
            MIJSharedPreference.delete(key.rawValue)
        } catch (e: Exception) {
            throw e
        }
    }

    /* == Int ==*/
    override fun loadInt(key: IntKey, default: Int): Int {
        try {
            return MIJSharedPreference.read(key.rawValue, default)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun saveInt(key: IntKey, value: Int) {
        try {
            MIJSharedPreference.save(key.rawValue, value)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun clearInt(key: IntKey) {
        try {
            MIJSharedPreference.delete(key.rawValue)
        } catch (e: Exception) {
            throw e
        }
    }
}