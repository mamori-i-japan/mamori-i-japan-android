package jp.co.tracecovid19.data.api.trace

import io.reactivex.Single
import jp.co.tracecovid19.data.model.TempUserId
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface TraceApiService {
    @POST("api/v1/upload")
    fun upload(@Body hoge: String): Single<Any>
    @GET("dev/users/me/temp_ids")
    fun fetchTempIds(@Header("x-mobile-secret-random-token") random: String = "helloworld"): Single<List<TempUserId>>
}