package jp.mamori_i.app.data.repository.profile

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Single
import jp.mamori_i.app.data.exception.MIJException
import jp.mamori_i.app.data.exception.MIJException.Reason.*
import jp.mamori_i.app.data.model.Profile

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
                result.onError(MIJException(Network))
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
                        } ?: result.onError(MIJException(Network)) // データなしはパースエラーとする
                    }
                    .addOnFailureListener { e ->
                        result.onError(e)
                    }
            } else {
                result.onError(MIJException(Network))
            }
        }
    }
}