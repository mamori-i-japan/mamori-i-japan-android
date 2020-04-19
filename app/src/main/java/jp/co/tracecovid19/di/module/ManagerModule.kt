package jp.co.tracecovid19.di.module

import jp.co.tracecovid19.idmanager.TempIdManager
import org.koin.dsl.module

val managerModule = module {
    single <TempIdManager> {
        TempIdManager(get(), get())
    }
}