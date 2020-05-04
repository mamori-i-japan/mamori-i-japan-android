package jp.mamori_i.app.data.repository.session

import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.mamori_i.app.data.api.login.LoginApiService
import jp.mamori_i.app.data.exception.MIJException
import jp.mamori_i.app.data.exception.MIJException.Reason.Auth
import jp.mamori_i.app.data.model.LoginRequestBody
import jp.mamori_i.app.data.model.PrefectureType


class SessionRepositoryImpl(private val auth: FirebaseAuth,
                            private val api: LoginApiService): SessionRepository {

    override fun isLogin(): Boolean {
        return auth.currentUser != null
    }

    override fun login(prefectureType: PrefectureType): Single<Boolean> {
        return Single.create { result ->
            auth.signInAnonymously().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.user?.getIdToken(false)?.result?.token?.let { token ->
                        val requestBody = LoginRequestBody(prefectureType.rawValue)
                        api.login("Bearer $token", requestBody)
                            .subscribeOn(Schedulers.io())
                            .subscribeBy (
                                onSuccess = {
                                    result.onSuccess(true)
                                },
                                onError = { e ->
                                    result.onError(e)
                                }
                            )
                    }?: result.onError(task.exception?: MIJException(Auth))
                } else {
                    result.onError(task.exception?: MIJException(Auth))                }
            }
        }
    }
}