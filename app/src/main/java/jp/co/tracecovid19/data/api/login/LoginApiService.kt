package jp.co.tracecovid19.data.api.login

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {
    @POST("dev/auth/login")
    fun login(@Body prefecture: Int, job: String?): Single<Any>
}