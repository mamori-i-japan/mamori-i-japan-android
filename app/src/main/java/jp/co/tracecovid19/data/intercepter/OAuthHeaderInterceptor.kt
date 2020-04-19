package jp.co.tracecovid19.data.intercepter

import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response

class OAuthHeaderInterceptor(private val auth: FirebaseAuth) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        // トークンをヘッダーにセットする
        auth.currentUser?.getIdToken(false)?.result?.token?.let {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $it")
                .build()
        }
        return chain.proceed(request)
    }
}