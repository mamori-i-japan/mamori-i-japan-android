package jp.co.tracecovid19.data.model

import com.squareup.moshi.Json

/**
 * Created by knakahir on 2019/02/03.
 */
class LoginRequest {
    data class SignInRequestBody(val email: String,
                                 val password: String)

    data class SignUpRequestBody(val email: String,
                                 val password: String,
                                 @Json(name = "password_confirmation") val confirmPassword: String)

    data class ConfirmIdentificationRequestBody(@Json(name = "confirmation_token") val confirmationToken: String)
}