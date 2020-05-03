package jp.mamori_i.app.data.storage

import android.content.Context
import androidx.core.content.edit
import org.json.JSONArray


class MIJSharedPreferenceImpl(context: Context): MIJSharedPreference {
    private  val KEY = "jp.mamori_i.app.shared"
    private val prefs = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)

    override fun <T: Any> read(key: String, default: T): T {
        try {
            return when(default) {
                is String -> { prefs.getString(key, default) as T}
                is Boolean -> { prefs.getBoolean(key, default) as T }
                is List<*> -> {
                    val jsonArray = JSONArray(prefs.getString(key, "[]"))
                    val mutableList = mutableListOf<String>()
                    for (i in 0 until jsonArray.length()) {
                        mutableList.add(jsonArray.get(i) as String)
                    }
                    return mutableList as T
                }
                else -> default
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun <T: Any> save(key: String, value: T) {
        try {
            prefs.edit {
                when(value) {
                    is String -> { putString(key, value) }
                    is Boolean -> { putBoolean(key, value) }
                    is List<*> -> {
                        val jsonArray = JSONArray(value)
                        putString(key, jsonArray.toString())
                    }
                    else -> {}
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun delete(key: String) {
        try {
            prefs.edit {
                remove(key)
            }
        } catch (e: Exception) {
            throw e
        }
    }
}