package jp.co.tracecovid19.data.repository.config

import android.app.Activity
import io.reactivex.Single
import jp.co.tracecovid19.data.model.AndroidAppStatus
import jp.co.tracecovid19.data.model.RemoteConfig

interface ConfigRepository {
    fun fetchAppStatus(activity: Activity): Single<AndroidAppStatus>
}