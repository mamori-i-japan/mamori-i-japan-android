package jp.mamori_i.app.data.repository.config

import android.app.Activity
import io.reactivex.Single
import jp.mamori_i.app.data.model.AndroidAppStatus

interface ConfigRepository {
    fun fetchAppStatus(activity: Activity): Single<AndroidAppStatus>
}