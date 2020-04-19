package jp.co.tracecovid19

import android.content.Context

// TODO: 移行してきただけなので、後で適切な仕組みにして削除
object BlueTracePreference {

    private const val PREF_ID = "Tracer_pref"
    private const val NEXT_FETCH_TIME = "NEXT_FETCH_TIME"
    private const val EXPIRY_TIME = "EXPIRY_TIME"
    private const val LAST_FETCH_TIME = "LAST_FETCH_TIME"
    private const val LAST_PURGE_TIME = "LAST_PURGE_TIME"

    fun putNextFetchTimeInMillis(context: Context, time: Long) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().putLong(NEXT_FETCH_TIME, time).apply()
    }

    fun putExpiryTimeInMillis(context: Context, time: Long) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().putLong(EXPIRY_TIME, time).apply()
    }

    fun getNextFetchTimeInMillis(context: Context): Long {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .getLong(
                NEXT_FETCH_TIME, 0
            )
    }

    fun getExpiryTimeInMillis(context: Context): Long {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .getLong(
                EXPIRY_TIME, 0
            )
    }

    fun putLastFetchTimeInMillis(context: Context, time: Long) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().putLong(LAST_FETCH_TIME, time).apply()
    }

    fun getLastPurgeTime(context: Context): Long {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .getLong(LAST_PURGE_TIME, 0)
    }

    fun putLastPurgeTime(context: Context, lastPurgeTime: Long) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().putLong(LAST_PURGE_TIME, lastPurgeTime).apply()
    }
}
