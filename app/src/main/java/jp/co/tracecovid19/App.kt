package jp.co.tracecovid19

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import jp.co.tracecovid19.di.module.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application() {

    companion object {
        private const val TAG = "App"

        lateinit var AppContext: Context
    }

    override fun onCreate() {
        super.onCreate()

        AppContext = applicationContext

        startKoin {
            androidContext(applicationContext)
            modules(
                listOf(
                    appModule,
                    dataModule,
                    repositoryModule,
                    serviceModule,
                    viewModule,
                    databaseModule,
                    daoModule,
                    managerModule
                )
            )
        }

        // TODO
        FirebaseApp.initializeApp(AppContext)
    }
}