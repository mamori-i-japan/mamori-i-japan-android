package jp.co.tracecovid19.data.api.login

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// TODO 現時点では適当です。
interface LoginApiService {
    @POST("dev/auth/login")
    fun login(@Header("x-mobile-secret-random-token") random: String = "helloworld"): Single<Any>
}