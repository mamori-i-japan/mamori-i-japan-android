package jp.mamori_i.app.data.repository.profile

import android.app.Activity
import io.reactivex.Single
import jp.mamori_i.app.data.model.Profile

interface ProfileRepository {
    // プロフィールの更新
    fun updateProfile(profile: Profile, activity: Activity): Single<Boolean>
    // プロフィールの取得
    fun fetchProfile(activity: Activity): Single<Profile>
}