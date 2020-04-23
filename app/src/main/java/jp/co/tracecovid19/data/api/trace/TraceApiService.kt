package jp.co.tracecovid19.data.api.trace

import io.reactivex.Single
import jp.co.tracecovid19.data.model.TempUserId
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface TraceApiService {
    @POST("api/v1/upload")
    fun upload(@Header("Authorization") authorization: String, @Body hoge: String): Single<Any>
    @GET("dev/users/me/temp_ids")
    fun fetchTempIds(@Header("Authorization") authorization: String): Single<List<TempUserId>>
}