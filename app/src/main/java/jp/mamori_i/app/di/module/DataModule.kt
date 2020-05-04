package jp.mamori_i.app.di.module

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import jp.mamori_i.app.BuildConfig
import jp.mamori_i.app.data.storage.MIJKeyStore
import jp.mamori_i.app.data.storage.MIJKeyStoreImpl
import jp.mamori_i.app.data.storage.MIJSharedPreference
import jp.mamori_i.app.data.storage.MIJSharedPreferenceImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


val dataModule = module {
    single <MIJSharedPreference> {
        MIJSharedPreferenceImpl(androidContext())
    }

    single <MIJKeyStore> {
        MIJKeyStoreImpl()
    }

    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = when(BuildConfig.BUILD_TYPE) {
                    "debug","debugMinify" -> HttpLoggingInterceptor.Level.BODY
                    else -> HttpLoggingInterceptor.Level.NONE
                }
            })
            .build()
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
