package jp.mamori_i.app.data.repository.profile

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.mamori_i.app.data.api.profile.ProfileApiService
import jp.mamori_i.app.data.exception.MIJException
import jp.mamori_i.app.data.exception.MIJException.Reason.*
import jp.mamori_i.app.data.model.PrefectureType
import jp.mamori_i.app.data.model.Profile
import jp.mamori_i.app.data.model.UpdateProfileRequestBody

class ProfileRepositoryImpl(private val fireStore: FirebaseFirestore,
                            private val api: ProfileApiService,
                            private val auth: FirebaseAuth): ProfileRepository {

    override fun updatePrefecture(prefecture: PrefectureType): Single<Boolean> {
        return Single.create { result ->
            auth.currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.token?.let { token ->
                        val requestBody = UpdateProfileRequestBody(prefecture.rawValue)
                        api.updateProfile("Bearer $token", requestBody)
                            .subscribeOn(Schedulers.io())
                            .subscribeBy (
                                onSuccess = {
                                    result.onSuccess(true)
                                },
                                onError = { e ->
                                    result.onError(e)
                                }
                            )
                    }?: result.onError(task.exception?: MIJException(Auth))
                } else {
                    result.onError(task.exception?: MIJException(Auth))
                }
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
                        try {
                            document.toObject(Profile::class.java)?.let {
                                result.onSuccess(it)
                            } ?: result.onError(MIJException(Parse)) // データなしはパースエラーとする
                        } catch (e: Throwable) {
                            result.onError(e)
                        }
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