package jp.co.tracecovid19.data.repository.profile

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.JsonParseException
import io.reactivex.Single
import jp.co.tracecovid19.data.model.Profile

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
                    result.onError(e)
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
                    } ?: result.onError(JsonParseException(""))
                }
                .addOnFailureListener { e ->
                    result.onError(e)
                }
        }
    }
}