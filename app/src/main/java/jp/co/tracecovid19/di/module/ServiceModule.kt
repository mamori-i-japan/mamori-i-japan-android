package jp.co.tracecovid19.di.module

import com.google.firebase.storage.FirebaseStorage
import jp.co.tracecovid19.data.api.login.LoginApiService
import jp.co.tracecovid19.data.api.trace.TraceApiService
import jp.co.tracecovid19.data.storage.*
import org.koin.dsl.module
import retrofit2.Retrofit


val serviceModule = module {
    factory <LoginApiService> {
        (get() as Retrofit).create(LoginApiService::class.java)
    }

    factory <TraceApiService> {
        (get() as Retrofit).create(TraceApiService::class.java)
    }

    factory <LocalStorageService> {
        LocalStorageServiceImpl(get())
    }

    factory <LocalCacheService> {
        LocalCacheServiceImpl()
    }

    factory <FirebaseStorageService> {
        FirebaseStorageServiceImpl(FirebaseStorage.getInstance())
    }
}
