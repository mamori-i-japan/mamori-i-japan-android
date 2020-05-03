package jp.mamori_i.app.data.api.profile

import io.reactivex.Single
import jp.mamori_i.app.data.model.LoginRequestBody
import jp.mamori_i.app.data.model.UpdateProfileRequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

interface ProfileApiService {
    @PATCH("users/me/profile")
    fun updateProfile(@Header("Authorization") authorization: String, @Body body: UpdateProfileRequestBody): Single<Any>
}