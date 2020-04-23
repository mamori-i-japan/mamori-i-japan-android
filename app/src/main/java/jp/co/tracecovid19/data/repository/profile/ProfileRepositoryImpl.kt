package jp.co.tracecovid19.data.repository.profile

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Single
import jp.co.tracecovid19.data.exception.TraceCovid19Exception
import jp.co.tracecovid19.data.exception.TraceCovid19Exception.Reason.*
import jp.co.tracecovid19.data.model.Profile

class ProfileRepositoryImpl(private val fireStore: FirebaseFirestore,
                            private val auth: FirebaseAuth): ProfileRepository {

    override fun updateProfile(profile: Profile, activity: Activity): Single<Boolean> {
        return Single.create { result ->
            val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            if (activeNetwork?.isConnectedOrConnecting == true) {
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
            } else {
                result.onError(TraceCovid19Exception(Network))
            }
        }
    }

    override fun fetchProfile(activity: Activity): Single<Profile> {
        return Single.create { result ->
            val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            if (activeNetwork?.isConnectedOrConnecting == true) {
                fireStore.collection("users")
                    .document(auth.uid ?: "")
                    .collection("profile")
                    .document(auth.uid ?: "")
                    .get()
                    .addOnSuccessListener { document ->
                        document.toObject(Profile::class.java)?.let {
                            result.onSuccess(it)
                        } ?: result.onError(TraceCovid19Exception(Network)) // データなしはパースエラーとする
                    }
                    .addOnFailureListener { e ->
                        result.onError(e)
                    }
            } else {
                result.onError(TraceCovid19Exception(Network))
            }
        }
    }
}