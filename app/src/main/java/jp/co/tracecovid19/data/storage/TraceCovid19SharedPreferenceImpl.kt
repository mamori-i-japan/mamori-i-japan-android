package jp.co.tracecovid19.data.storage

import android.content.Context
import androidx.core.content.edit


class TraceCovid19SharedPreferenceImpl(context: Context): TraceCovid19SharedPreference {
    private  val KEY = "jp.co.tracecovid19.shared"
    private val prefs = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)

    override fun <T: Any> read(key: String, default: T): T {
        try {
            return when(default) {
                is String -> { prefs.getString(key, default) as T}
                is Boolean -> { prefs.getBoolean(key, default) as T }
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