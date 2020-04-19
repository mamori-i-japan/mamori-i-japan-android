package jp.co.tracecovid19.di.module

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import jp.co.tracecovid19.data.repository.config.ConfigRepository
import jp.co.tracecovid19.data.repository.config.ConfigRepositoryImpl
import jp.co.tracecovid19.data.repository.session.SessionRepository
import jp.co.tracecovid19.data.repository.session.SessionRepositoryImpl
import jp.co.tracecovid19.data.repository.profile.ProfileRepository
import jp.co.tracecovid19.data.repository.profile.ProfileRepositoryImpl
import jp.co.tracecovid19.data.repository.trase.TraceRepository
import jp.co.tracecovid19.data.repository.trase.TraceRepositoryImpl
import org.koin.dsl.module


val repositoryModule = module {

    factory <ConfigRepository> {
        ConfigRepositoryImpl(get(), get(), get())
    }

    factory <SessionRepository> {
        SessionRepositoryImpl(PhoneAuthProvider.getInstance(), FirebaseAuth.getInstance(), get())
    }

    factory <ProfileRepository> {
        ProfileRepositoryImpl(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance())
    }

    factory <TraceRepository> {
        TraceRepositoryImpl(get(), get(), get(), get(), get())
    }
}