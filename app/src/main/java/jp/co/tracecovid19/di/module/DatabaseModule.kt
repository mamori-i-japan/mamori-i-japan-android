package jp.co.tracecovid19.di.module

import androidx.room.Room
import jp.co.tracecovid19.data.database.TraceCovid19Database
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { Room.databaseBuilder(androidContext(), TraceCovid19Database::class.java, "TraceCovid19Database").build() }
}