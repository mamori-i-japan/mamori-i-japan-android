package jp.mamori_i.app.screen.menu

import android.app.Activity
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import jp.mamori_i.app.data.model.Profile
import jp.mamori_i.app.data.repository.profile.ProfileRepository
import jp.mamori_i.app.screen.common.LogoutHelper
import jp.mamori_i.app.screen.common.MIJError
import jp.mamori_i.app.screen.menu.MenuListItemView.*
import jp.mamori_i.app.screen.menu.MenuListItemView.MenuListItemType.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


class MenuViewModel(private val profileRepository: ProfileRepository,
                    private val logoutHelper: LogoutHelper,
                    private val disposable: CompositeDisposable): ViewModel() {

    lateinit var navigator: MenuNavigator
    val menuItems = PublishSubject.create<List<MenuListItem>>()
    val fetchError = PublishSubject.create<MIJError>()

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

    fun fetchProfile(activity: Activity) {
        profileRepository.fetchProfile(activity)
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = {
                    menuItems.onNext(createMenuItems(it))
                },
                onError = { e ->
                    val reason = MIJError.mappingReason(e)
                    if (reason == MIJError.Reason.Auth) {
                        // 認証エラーの場合はログアウト処理をする
                        runBlocking (Dispatchers.IO) {
                            logoutHelper.logout()
                            fetchError.onNext(MIJError(reason, "TODO",
                                MIJError.Action.DialogLogout
                            ))
                        }
                    } else {
                        // 認証エラー以外はProfileがないものとしてそのまま進める
                        menuItems.onNext(createMenuItems(null))
                    }
                }
            ).addTo(disposable)
    }

    private fun createMenuItems(profile: Profile?): List<MenuListItem> {
        val items = mutableListOf<MenuListItem>()

        items.add(MenuListItem(Setting, "設定") {
            navigator.goToSetting()
        })

        if (!profile?.organizationCode.isNullOrEmpty()) {
            items.add(MenuListItem(DataUpload, "データのアップロード") {
                navigator.goToTraceDataUpload()
            })
        }

        items.add(MenuListItem(About, "このアプリについて") {
            navigator.goToAbout()
        })

        items.add(MenuListItem(License, "利用ライセンス") {
            navigator.goToLicense()
        })

        // TODO Debug
        items.add(MenuListItem(Logout, "(Debug)ログアウト") {
            runBlocking (Dispatchers.IO) {
                logoutHelper.logout()
            }
            navigator.goToSplash()
        })

        // TODO Debug
        items.add(MenuListItem(Restart, "(Debug)再起動") {
            navigator.goToSplash()
        })

        return items
    }
}