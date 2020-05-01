package jp.mamori_i.app.screen.start

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

class TutorialViewModel(private val disposable: CompositeDisposable): ViewModel() {

    lateinit var navigator: TutorialNavigator

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    fun onClickStart() {
        navigator.goToInputPrefecture()
    }

    fun onClickWebLink() {
        // TODO URL
        navigator.openWebBrowser("https://yahoo.co.jp".toUri())
    }
}