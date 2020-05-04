package jp.mamori_i.app.screen.common

import android.accounts.NetworkErrorException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.gson.JsonParseException
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.squareup.moshi.JsonDataException
import jp.mamori_i.app.data.exception.MIJException
import jp.mamori_i.app.data.exception.MIJException.Reason.*
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by knakahir on 2019/01/10.
 */
data class MIJError (val reason: Reason,
                     val message: String,
                     val description: String,
                     val action: Action,
                     val closeAction: (() -> Unit)? = null) {

    enum class Reason {
        Maintenance,
        Version,
        NetWork,
        Parse,
        DB,
        Auth,
        Business,
        Other
    }

    enum class Action {
        None,
        DialogCloseOnly,
        DialogRetry,
        DialogLogout,
        DialogBack,
        DialogAppKill,
        Inline,
        InView,
        ForceLogout,
        ForceScreenBack
    }

    companion object {
        // throwableに対してReasonのマッピング
        fun mappingReason(e: Throwable): Reason {
            return when(e) {
                is MIJException -> {
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
                    when (e.code()) {
                        400 -> {
                            Reason.Business
                        }
                        401 -> {
                            Reason.Auth
                        }
                        else -> {
                            Reason.Other
                        }
                    }
                }

                is JsonDataException ,
                is JsonParseException -> Reason.Parse

                is FirebaseAuthException -> Reason.Auth

                else -> Reason.Other
            }
        }
    }
}