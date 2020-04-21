package jp.co.tracecovid19.data.repository.session

import android.app.Activity
import io.reactivex.Single
import jp.co.tracecovid19.data.model.*


interface SessionRepository {
    // ログイン判定
    fun isLogin(): Boolean
    // トークン取得
    fun getToken(): Token?
    // 電話番号認証
    fun authPhoneNumber(tel: String, activity: Activity): Single<PhoneNumberAuthResult>
    // SMS認証
    fun authSms(inputCode: String, verificationId: String, activity: Activity): Single<Boolean>
    // ログイン
    fun login(prefectureType: PrefectureType, job: String?): Single<Boolean>
}