package jp.mamori_i.app.data.storage

import jp.mamori_i.app.data.storage.LocalStorageService.*
import jp.mamori_i.app.util.SecurityUtil

class LocalStorageServiceImpl(private val sharedPreference: MIJSharedPreference,
                              private val keyStore: MIJKeyStore): LocalStorageService {

    /* == List ==*/
    override fun loadList(key: ListKey, default: List<String>): List<String> {
        try {
            val hoge = sharedPreference.read(key.rawValue, listOf<String>())
            return sharedPreference.read(key.rawValue, listOf<String>()).map {
                SecurityUtil.decrypt(keyStore.privateKey(), it)?: ""
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun addList(key: ListKey, value: String) {
        try {
            val mutableList = loadList(key, listOf()).toMutableList()
            mutableList.add(value)
            val saveList =  mutableList.map { SecurityUtil.encrypt(keyStore.publicKey(), it)?: "" }
            sharedPreference.save(key.rawValue, saveList)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun clearList(key: ListKey) {
        try {
            sharedPreference.delete(key.rawValue)
        } catch (e: Exception) {
            throw e
        }
    }

    /* == Boolean ==*/
    override fun loadBoolean(key: BooleanKey, default: Boolean): Boolean {
        try {
            return sharedPreference.read(key.rawValue, default)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun saveBoolean(key: BooleanKey, value: Boolean) {
        try {
            sharedPreference.save(key.rawValue, value)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun clearBoolean(key: BooleanKey) {
        try {
            sharedPreference.delete(key.rawValue)
        } catch (e: Exception) {
            throw e
        }
    }

    /* == String ==*/
    override fun loadString(key: StringKey, default: String): String {
        try {
            return SecurityUtil.decrypt(keyStore.privateKey(), sharedPreference.read(key.rawValue, default))?: ""
        } catch (e: Exception) {
            throw e
        }
    }

    override fun saveString(key: StringKey, value: String) {
        try {
            SecurityUtil.encrypt(keyStore.publicKey(), value)?.let {
                sharedPreference.save(key.rawValue, it)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun clearString(key: StringKey) {
        try {
            sharedPreference.delete(key.rawValue)
        } catch (e: Exception) {
            throw e
        }
    }

    /* == Int ==*/
    override fun loadInt(key: IntKey, default: Int): Int {
        try {
            return sharedPreference.read(key.rawValue, default)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun saveInt(key: IntKey, value: Int) {
        try {
            sharedPreference.save(key.rawValue, value)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun clearInt(key: IntKey) {
        try {
            sharedPreference.delete(key.rawValue)
        } catch (e: Exception) {
            throw e
        }
    }
}