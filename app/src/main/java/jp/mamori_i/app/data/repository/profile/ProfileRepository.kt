package jp.mamori_i.app.data.repository.profile

import android.app.Activity
import io.reactivex.Single
import jp.mamori_i.app.data.model.PrefectureType
import jp.mamori_i.app.data.model.Profile

interface ProfileRepository {
    // 都道府県の更新
    fun updatePrefecture(prefecture: PrefectureType): Single<Boolean>
    // 組織コードの更新
    fun updateOrganizationCode(organizationCode: String): Single<Boolean>
    // 組織コードのクリア
    fun clearOrganizationCode(): Single<Boolean>
    // プロフィールの取得
    fun fetchProfile(activity: Activity): Single<Profile>
}