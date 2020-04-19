package jp.co.tracecovid19.data.repository.profile

import android.app.Activity
import io.reactivex.Single
import jp.co.tracecovid19.data.model.Profile

interface ProfileRepository {
    // プロフィールの更新
    fun updateProfile(profile: Profile, activity: Activity): Single<Boolean>
    // プロフィールの取得
    fun fetchProfile(activity: Activity): Single<Profile>
}