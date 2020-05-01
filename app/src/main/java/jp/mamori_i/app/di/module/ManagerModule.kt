package jp.mamori_i.app.di.module

import jp.mamori_i.app.idmanager.TempIdManager
import org.koin.dsl.module

val managerModule = module {
    single <TempIdManager> {
        TempIdManager(get())
    }
}