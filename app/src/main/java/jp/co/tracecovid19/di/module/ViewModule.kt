package jp.co.tracecovid19.di.module

import jp.co.tracecovid19.screen.common.LogoutHelper
import jp.co.tracecovid19.screen.common.LogoutHelperImpl
import jp.co.tracecovid19.screen.home.HomeViewModel
import jp.co.tracecovid19.screen.home.TestBLEViewModel
import jp.co.tracecovid19.screen.home.TestContactListViewModel
import jp.co.tracecovid19.screen.trace.TraceDataUploadViewModel
import jp.co.tracecovid19.screen.trace.TraceNotificationViewModel
import jp.co.tracecovid19.screen.menu.MenuViewModel
import jp.co.tracecovid19.screen.menu.SettingViewModel
import jp.co.tracecovid19.screen.start.SplashViewModel
import jp.co.tracecovid19.screen.register.InputPhoneNumberViewModel
import jp.co.tracecovid19.screen.register.AuthSmsViewModel
import jp.co.tracecovid19.screen.profile.InputPrefectureViewModel
import jp.co.tracecovid19.screen.profile.InputJobViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModule = module {

    /* Helpers */
    factory<LogoutHelper> {
        LogoutHelperImpl(get(), get(), get())
    }

    /* ViewModels */
    viewModel {
        HomeViewModel(get(), get())
    }
    viewModel {
        TestBLEViewModel(get())
    }
    viewModel {
        MenuViewModel(get())
    }
    viewModel {
        SettingViewModel(get(), get())
    }
    viewModel {
        AuthSmsViewModel(get(), get(), get())
    }
    viewModel {
        InputPhoneNumberViewModel(get(), get())
    }
    viewModel {
        InputPrefectureViewModel(get(), get())
    }
    viewModel {
        InputJobViewModel(get(), get())
    }
    viewModel {
        SplashViewModel(get(), get(), get(), get())
    }
    viewModel {
        TraceDataUploadViewModel()
    }
    viewModel {
        TraceNotificationViewModel()
    }
    viewModel {
        TestContactListViewModel(get(), get())
    }
}
