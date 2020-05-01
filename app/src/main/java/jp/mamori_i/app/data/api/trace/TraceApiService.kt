package jp.mamori_i.app.data.api.trace

import io.reactivex.Single
import jp.mamori_i.app.data.model.TempUserId
import jp.mamori_i.app.data.model.UploadDeepContactsRequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface TraceApiService {
    @POST("users/me/close_contacts")
    fun uploadDeepContacts(@Header("Authorization") authorization: String, @Body requestBody: UploadDeepContactsRequestBody): Single<Any>
}