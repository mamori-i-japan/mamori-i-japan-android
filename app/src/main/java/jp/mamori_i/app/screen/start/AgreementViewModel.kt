package jp.mamori_i.app.screen.start

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import jp.mamori_i.app.data.model.PrefectureType
import jp.mamori_i.app.data.repository.session.SessionRepository
import jp.mamori_i.app.screen.common.LogoutHelper
import jp.mamori_i.app.screen.common.MIJError
import jp.mamori_i.app.screen.common.MIJError.Action.DialogBack
import jp.mamori_i.app.screen.common.MIJError.Action.DialogRetry
import jp.mamori_i.app.screen.common.MIJError.Reason.NetWork

class AgreementViewModel(private val sessionRepository: SessionRepository,
                         private val logoutHelper: LogoutHelper,
                         private val disposable: CompositeDisposable): ViewModel() {

    lateinit var navigator: AgreementNavigator
    val loginError = PublishSubject.create<MIJError>()

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    fun onClickAgree(prefectureType: PrefectureType) {
        navigator.showProgress()
        sessionRepository.login(prefectureType)
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = {
                    navigator.hideProgress()
                    navigator.goToPermissionSetting()
                },
                onError = { e ->
                    navigator.hideProgress()
                    val reason = MIJError.mappingReason(e)
                    loginError.onNext(
                        when (reason) {
                            NetWork -> MIJError(reason, "文言検討12", DialogRetry)
                            else -> MIJError(reason, "文言検討13", DialogBack)
                        })
                }
            ).addTo(disposable)
    }

    fun onClickAgreementLink() {
        navigator.openWebBrowser("https://yahoo.co.jp".toUri())
    }
}