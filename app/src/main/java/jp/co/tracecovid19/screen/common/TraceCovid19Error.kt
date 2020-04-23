package jp.co.tracecovid19.screen.common

import android.accounts.NetworkErrorException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.gson.JsonParseException
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.squareup.moshi.JsonDataException
import jp.co.tracecovid19.data.exception.TraceCovid19Exception
import jp.co.tracecovid19.data.exception.TraceCovid19Exception.Reason.*
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by knakahir on 2019/01/10.
 */
data class TraceCovid19Error (val reason: Reason, val message: String, val action: Action) {

    enum class Reason {
        NetWork,
        Parse,
        DB,
        Auth,
        SMSSendLimit,
        SMSCodeExpired,
        SMSCodeUnmatched,
        Other
    }

    enum class Action {
        None,
        DialogCloseOnly,
        DialogRetry,
        DialogLogout,
        DialogBack,
        Inline,
        InView,
        ForceLogout,
        ForceScreenBack
    }

    companion object {
        // throwableに対してReasonのマッピング
        // TODO FirebaseからのSMSコード周りのエラーのマッピング
        // TODO DBエラーのマッピング
        fun mappingReason(e: Throwable): Reason {
            return when(e) {
                is TraceCovid19Exception -> {
                    when(e.reason) {
                        Network -> Reason.NetWork
                        Parse -> Reason.Parse
                        Auth -> Reason.Auth
                        Other -> Reason.Other
                    }
                }

                is NetworkErrorException,
                is FirebaseNetworkException,
                is UnknownHostException,
                is SocketTimeoutException -> Reason.NetWork

                is HttpException -> {
                    if (e.code() in 400 until 500) {
                        Reason.Auth
                    } else {
                        Reason.Other
                    }
                }

                is JsonDataException ,
                is JsonParseException -> Reason.Parse

                is FirebaseTooManyRequestsException -> Reason.SMSSendLimit

                is FirebaseAuthInvalidCredentialsException -> {
                    when(e.errorCode) {
                        "ERROR_SESSION_EXPIRED" -> Reason.SMSCodeExpired
                        "ERROR_INVALID_VERIFICATION_CODE" -> Reason.SMSCodeUnmatched
                        else -> Reason.Other
                    }
                }

                is FirebaseAuthException -> Reason.Auth

                else -> Reason.Other
            }
        }
    }
}