package jp.co.tracecovid19.di.module

import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import jp.co.tracecovid19.BuildConfig
import jp.co.tracecovid19.data.client.getUnsafeOkHttpClientBuilder
import jp.co.tracecovid19.data.intercepter.CommonHeaderInterceptor
import jp.co.tracecovid19.data.intercepter.OAuthHeaderInterceptor
import jp.co.tracecovid19.data.storage.KeyStoreManager
import jp.co.tracecovid19.data.storage.KeyStoreManagerImpl
import jp.co.tracecovid19.data.storage.SharedPreferenceManager
import jp.co.tracecovid19.data.storage.SharedPreferenceManagerImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


val dataModule = module {
    single {
        CommonHeaderInterceptor()
    }

    single {
        OAuthHeaderInterceptor(FirebaseAuth.getInstance())
    }

    single <SharedPreferenceManager> {
        SharedPreferenceManagerImpl(androidContext())
    }

    single <KeyStoreManager> {
        KeyStoreManagerImpl()
    }

    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    if (BuildConfig.IS_IGONORE_SSL_ERROR) {
        single {
            getUnsafeOkHttpClientBuilder() // SSL無視
                .addInterceptor(get() as CommonHeaderInterceptor)
                .addInterceptor(get() as OAuthHeaderInterceptor)
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY // ログを出力
                })
                .build()
        }
    } else {
        single {
            OkHttpClient.Builder()
                .addInterceptor(get() as CommonHeaderInterceptor)
                .addInterceptor(get() as OAuthHeaderInterceptor)
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
