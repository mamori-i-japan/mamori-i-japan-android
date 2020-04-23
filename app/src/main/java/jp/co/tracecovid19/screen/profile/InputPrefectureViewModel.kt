package jp.co.tracecovid19.screen.profile

import android.app.Activity
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import jp.co.tracecovid19.data.model.PrefectureType
import jp.co.tracecovid19.data.model.Profile
import jp.co.tracecovid19.screen.common.TraceCovid19Error
import jp.co.tracecovid19.screen.common.TraceCovid19Error.Reason.*
import jp.co.tracecovid19.screen.common.TraceCovid19Error.Action.*
import jp.co.tracecovid19.data.repository.profile.ProfileRepository
import jp.co.tracecovid19.screen.common.LogoutHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class InputPrefectureViewModel(private val profileRepository: ProfileRepository,
                               private val logoutHelper: LogoutHelper,
                               private val disposable: CompositeDisposable): ViewModel() {

    lateinit var navigator: InputPrefectureNavigator
    val updateError = PublishSubject.create<TraceCovid19Error>()

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

    fun onClickExecuteButton(inputPrefecture: PrefectureType?,
                             profile: Profile,
                             isRegistrationFlow: Boolean,
                             activity: Activity) {
        inputPrefecture?.let {
            profile.prefecture = it.rawValue
            if (isRegistrationFlow) {
                navigator.goToInputWork(InputJobTransitionEntity(profile, isRegistrationFlow))
            } else {
                navigator.showProgress()
                profileRepository.updateProfile(profile, activity)
                    .subscribeOn(Schedulers.io())
                    .subscribeBy(
                    onSuccess = {
                        navigator.hideProgress()
                        navigator.finishWithCompleteMessage("更新しました。") // TODO メッセージ
                    },
                    onError = { e ->
                        navigator.hideProgress()
                        val reason = TraceCovid19Error.mappingReason(e)
                        if (reason == Auth) {
                            // 認証エラーの場合はログアウト処理をする
                            runBlocking (Dispatchers.IO) {
                                logoutHelper.logout()
                            }
                        }
                        updateError.onNext(
                            when (reason) {
                                NetWork -> TraceCovid19Error(reason, "文言検討1", DialogCloseOnly)
                                Auth -> TraceCovid19Error(reason, "文言検討22", DialogLogout)
                                else -> TraceCovid19Error(reason, "文言検討2", DialogCloseOnly)
                            })
                    }
                ).addTo(disposable)
            }
        }
    }
}