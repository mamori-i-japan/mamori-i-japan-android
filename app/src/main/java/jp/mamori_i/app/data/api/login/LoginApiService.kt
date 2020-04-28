package jp.mamori_i.app.data.api.login

import io.reactivex.Single
import jp.mamori_i.app.data.model.LoginRequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface LoginApiService {
    @POST("dev/auth/login")
    fun login(@Header("Authorization") authorization: String, @Body body: LoginRequestBody): Single<Any>
}