package jp.mamori_i.app.di.module

import androidx.room.Room
import jp.mamori_i.app.data.database.MIJDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { Room.databaseBuilder(androidContext(), MIJDatabase::class.java, "MIJDatabase").build() }
}