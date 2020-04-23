package jp.co.tracecovid19.screen.menu

import android.app.Activity
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import jp.co.tracecovid19.data.model.Profile
import jp.co.tracecovid19.data.repository.profile.ProfileRepository
import jp.co.tracecovid19.screen.common.TraceCovid19Error
import jp.co.tracecovid19.screen.common.TraceCovid19Error.Reason.*
import jp.co.tracecovid19.screen.common.TraceCovid19Error.Action.*
import jp.co.tracecovid19.screen.profile.InputPrefectureTransitionEntity
import jp.co.tracecovid19.screen.profile.InputJobTransitionEntity


class SettingViewModel(private val profileRepository: ProfileRepository,
                       private val disposable: CompositeDisposable): ViewModel() {

    lateinit var navigator: SettingNavigator
    val profile = PublishSubject.create<Profile>()
    val fetchError = PublishSubject.create<TraceCovid19Error>()

    private var _profile: Profile? = null

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

    fun fetchProfile(activity: Activity) {
        profileRepository.fetchProfile(activity)
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = {
                    _profile = it
                    profile.onNext(it)
                },
                onError = { e ->
                    val reason = TraceCovid19Error.mappingReason(e)
                    fetchError.onNext(
                        when (reason) {
                            NetWork -> TraceCovid19Error(reason, "文言検討20", DialogBack)
                            // TODO Auth -> TraceCovid19Error(reason, "文言検討3", DialogCloseOnly)
                            Parse -> TraceCovid19Error(reason, "文言検討21", DialogBack)
                            else -> TraceCovid19Error(reason, "文言検討21", DialogBack)
                        })
                }
            ).addTo(disposable)
    }

    fun onClickPrefecture() {
        _profile?.let {
            navigator.goToInputPrefecture(InputPrefectureTransitionEntity(it, false))
        }
    }

    fun onClickWork() {
        _profile?.let {
            navigator.goToInputWork(InputJobTransitionEntity(it, false))
        }
    }

}