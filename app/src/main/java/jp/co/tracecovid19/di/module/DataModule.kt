package jp.co.tracecovid19.di.module

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import jp.co.tracecovid19.BuildConfig
import jp.co.tracecovid19.data.client.getUnsafeOkHttpClientBuilder
import jp.co.tracecovid19.data.storage.TraceCovid19KeyStore
import jp.co.tracecovid19.data.storage.TraceCovid19KeyStoreImpl
import jp.co.tracecovid19.data.storage.TraceCovid19SharedPreference
import jp.co.tracecovid19.data.storage.TraceCovid19SharedPreferenceImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


val dataModule = module {
    single <TraceCovid19SharedPreference> {
        TraceCovid19SharedPreferenceImpl(androidContext())
    }

    single <TraceCovid19KeyStore> {
        TraceCovid19KeyStoreImpl()
    }

    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    if (BuildConfig.IS_IGONORE_SSL_ERROR) {
        single {
            getUnsafeOkHttpClientBuilder() // SSL無視
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY // ログを出力
                })
                .build()
        }
    } else {
        single {
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY // TODO ログを出力 消す
                })
                .build()
        }
    }

    single {
        Retrofit
            .Builder()
            .client(get())
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
}
