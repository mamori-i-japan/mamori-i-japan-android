package jp.co.tracecovid19.di.module

import io.reactivex.disposables.CompositeDisposable
import org.koin.dsl.module


val appModule = module {
    factory { CompositeDisposable() }
}



