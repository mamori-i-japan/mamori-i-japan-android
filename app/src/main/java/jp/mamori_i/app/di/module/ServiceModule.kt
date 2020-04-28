package jp.mamori_i.app.di.module

import com.google.firebase.storage.FirebaseStorage
import jp.mamori_i.app.data.api.login.LoginApiService
import jp.mamori_i.app.data.api.trace.TraceApiService
import jp.mamori_i.app.data.storage.*
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
