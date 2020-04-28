package jp.mamori_i.app.di.module

import io.reactivex.disposables.CompositeDisposable
import org.koin.dsl.module


val appModule = module {
    factory { CompositeDisposable() }
}



