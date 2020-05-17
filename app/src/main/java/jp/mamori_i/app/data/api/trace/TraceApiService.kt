package jp.mamori_i.app.data.api.trace

import io.reactivex.Single
import jp.mamori_i.app.data.model.DeleteTempIdsRequestBody
import jp.mamori_i.app.data.model.UploadTempIdsRequestBody
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST

interface TraceApiService {
    @POST("users/me/health_center_tokens")
    fun uploadTempIds(@Header("Authorization") authorization: String, @Body requestBody: UploadTempIdsRequestBody): Single<Any>

    @HTTP(method = "DELETE", path = "users/me/diagnosis_keys", hasBody = true)
    fun deleteTempIds(@Header("Authorization") authorization: String, @Body body: DeleteTempIdsRequestBody): Single<Any>
}