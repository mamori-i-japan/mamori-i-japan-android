package jp.mamori_i.app.data.repository.session

import android.app.Activity
import io.reactivex.Single
import jp.mamori_i.app.data.model.*


interface SessionRepository {
    // ログイン判定
    fun isLogin(): Boolean
    // ログイン
    fun login(prefectureType: PrefectureType): Single<Boolean>
}