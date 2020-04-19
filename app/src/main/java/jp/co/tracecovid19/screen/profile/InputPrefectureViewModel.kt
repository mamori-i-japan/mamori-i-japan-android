package jp.co.tracecovid19.screen.profile

import android.app.Activity
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import jp.co.tracecovid19.data.model.PrefectureType
import jp.co.tracecovid19.data.model.Profile
import jp.co.tracecovid19.data.model.TraceCovid19Error
import jp.co.tracecovid19.data.repository.profile.ProfileRepository

class InputPrefectureViewModel(private val profileRepository: ProfileRepository,
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
                profileRepository.updateProfile(profile, activity).subscribeBy(
                    onSuccess = {
                        navigator.hideProgress()
                        navigator.finishWithCompleteMessage("更新しました。") // TODO メッセージ
                    },
                    onError = { e ->
                        navigator.hideProgress()
                        // TODO エラー
                        (e as? TraceCovid19Error)?.let { error ->
                            updateError.onNext(error)
                        }?: updateError.onNext(TraceCovid19Error.unexpectedError())
                    }
                ).addTo(disposable)
            }
        }
    }
}