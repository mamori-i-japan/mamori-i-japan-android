package jp.co.tracecovid19.data.repository.profile

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Single
import jp.co.tracecovid19.data.model.Profile
import jp.co.tracecovid19.data.model.TraceCovid19Error

class ProfileRepositoryImpl(private val fireStore: FirebaseFirestore,
                            private val auth: FirebaseAuth): ProfileRepository {

    override fun updateProfile(profile: Profile, activity: Activity): Single<Boolean> {
        return Single.create { result ->
            fireStore.collection("users")
                .document(auth.uid ?: "")
                .collection("profile")
                .document(auth.uid ?: "")
                .set(profile)
                .addOnSuccessListener { _ ->
                    result.onSuccess(true)
                }
                .addOnFailureListener { e ->
                    result.onError(
                        TraceCovid19Error(
                            TraceCovid19Error.ErrorType.General,
                            e.localizedMessage
                        )
                    )
                }
        }
    }

    override fun fetchProfile(activity: Activity): Single<Profile> {
        return Single.create { result ->
            fireStore.collection("users")
                .document(auth.uid ?: "")
                .collection("profile")
                .document(auth.uid ?: "")
                .get()
                .addOnSuccessListener { document ->
                    document.toObject(Profile::class.java)?.let {
                        result.onSuccess(it)
                    } ?: result.onError(
                        TraceCovid19Error(
                            TraceCovid19Error.ErrorType.General,
                            "fireStoreのfetchに失敗"
                        )
                    )
                }
                .addOnFailureListener { e ->
                    result.onError(
                        TraceCovid19Error(
                            TraceCovid19Error.ErrorType.General,
                            e.localizedMessage
                        )
                    )
                }
        }
    }
}