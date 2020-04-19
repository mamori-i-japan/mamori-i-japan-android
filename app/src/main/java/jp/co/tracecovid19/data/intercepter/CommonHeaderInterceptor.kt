package jp.co.tracecovid19.data.intercepter

import okhttp3.Interceptor
import okhttp3.Response


// TODO (仮)なんかHeaderに入れたい場合に使う
class CommonHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // 現状特にやることはない
        val request = chain.request()
                .newBuilder()
                .build()
        return chain.proceed(request)
    }
}