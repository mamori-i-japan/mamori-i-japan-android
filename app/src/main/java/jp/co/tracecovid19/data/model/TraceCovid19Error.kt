package jp.co.tracecovid19.data.model

import com.google.gson.JsonParseException
import com.squareup.moshi.JsonDataException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by knakahir on 2019/01/10.
 */
data class TraceCovid19Error (val type: ErrorType,
                              override val message: String): Throwable() {

    enum class ErrorType {
        General,
        AppVersion,
        Maintenance,
        Auth,
        Parse,
        Network,
        Storage,
        System
    }

    companion object {
        // Throwableのエラーから作成
        fun create(e: Throwable): TraceCovid19Error {
            return when(e) {
                is JsonDataException ,
                is JsonParseException -> parseError()
                is UnknownHostException,
                is SocketTimeoutException,
                is HttpException -> networkError()
                else -> unexpectedError()
            }
        }

        fun unexpectedError(): TraceCovid19Error {
            return TraceCovid19Error(ErrorType.System, "システムエラー")
        }

        private fun parseError(): TraceCovid19Error {
            return TraceCovid19Error(ErrorType.Parse, "パースエラー")
        }

        private fun networkError(): TraceCovid19Error {
            return TraceCovid19Error(ErrorType.Network, "通信エラー")
        }

        private fun storageError(): TraceCovid19Error {
            return  TraceCovid19Error(ErrorType.Storage, "ストレージエラー")
        }
    }
}