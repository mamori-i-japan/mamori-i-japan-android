package jp.mamori_i.app.di.module

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import jp.mamori_i.app.data.repository.config.ConfigRepository
import jp.mamori_i.app.data.repository.config.ConfigRepositoryImpl
import jp.mamori_i.app.data.repository.session.SessionRepository
import jp.mamori_i.app.data.repository.session.SessionRepositoryImpl
import jp.mamori_i.app.data.repository.profile.ProfileRepository
import jp.mamori_i.app.data.repository.profile.ProfileRepositoryImpl
import jp.mamori_i.app.data.repository.trase.TraceRepository
import jp.mamori_i.app.data.repository.trase.TraceRepositoryImpl
import org.koin.dsl.module


val repositoryModule = module {

    factory <ConfigRepository> {
        ConfigRepositoryImpl(get(), get(), get())
    }

    factory <SessionRepository> {
        SessionRepositoryImpl(FirebaseAuth.getInstance(), get())
    }

    factory <ProfileRepository> {
        ProfileRepositoryImpl(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance())
    }

    factory <TraceRepository> {
        TraceRepositoryImpl(get(), get(), FirebaseAuth.getInstance(), get(), get(), get())
    }
}