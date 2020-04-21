package jp.co.tracecovid19.screen.common

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.gson.JsonParseException
import com.squareup.moshi.JsonDataException
import retrofit2.HttpException
import java.lang.Exception
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
        // TODO Firebaseからの認証エラーのマッピング
        // TODO FirebaseからのSMSコード周りのエラーのマッピング
        // TODO DBエラーのマッピング
        fun mappingReason(e: Throwable): Reason {
            return when(e) {
                is FirebaseNetworkException,
                is UnknownHostException,
                is SocketTimeoutException,
                is HttpException -> Reason.NetWork

                is JsonDataException ,
                is JsonParseException -> Reason.Parse

                is FirebaseTooManyRequestsException -> Reason.SMSSendLimit

                else -> Reason.Other
            }
        }
    }
}