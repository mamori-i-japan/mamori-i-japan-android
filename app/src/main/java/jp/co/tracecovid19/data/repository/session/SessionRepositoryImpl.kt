package jp.co.tracecovid19.data.repository.session

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.co.tracecovid19.data.api.login.LoginApiService
import jp.co.tracecovid19.data.exception.TraceCovid19Exception
import jp.co.tracecovid19.data.exception.TraceCovid19Exception.Reason.*
import jp.co.tracecovid19.data.model.LoginRequestBody
import jp.co.tracecovid19.data.model.PhoneNumberAuthResult
import jp.co.tracecovid19.data.model.PrefectureType
import java.util.concurrent.TimeUnit


class SessionRepositoryImpl(private val phoneAuthProvider: PhoneAuthProvider,
                            private val auth: FirebaseAuth,
                            private val api: LoginApiService): SessionRepository {

    override fun isLogin(): Boolean {
        return auth.currentUser != null
    }

    override fun authPhoneNumber(tel: String, activity: Activity): Single<PhoneNumberAuthResult> {
        return Single.create { result ->
            phoneAuthProvider.verifyPhoneNumber(
                "+81$tel",
                60,
                TimeUnit.SECONDS,
                activity,
                object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        auth.signInWithCredential(credential)
                            .addOnCompleteListener(activity) { task ->
                                if (task.isSuccessful) {
                                    result.onSuccess(PhoneNumberAuthResult(null))
                                } else {
                                    result.onError(task.exception?: TraceCovid19Exception(Auth))
                                }
                            }
                    }

                    override fun onVerificationFailed(error: FirebaseException) {
                        result.onError(error)
                    }

                    override fun onCodeSent(verificationId: String, resendToken: PhoneAuthProvider.ForceResendingToken) {
                        result.onSuccess(PhoneNumberAuthResult(verificationId))
                    }
                })
        }
    }

    override fun authSms(inputCode: String, verificationId: String, activity: Activity): Single<Boolean> {
        return Single.create { result ->
            val credential = PhoneAuthProvider.getCredential(verificationId, inputCode)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        result.onSuccess(true)
                    } else {
                        result.onError(task.exception?: TraceCovid19Exception(Auth))
                    }
                }
        }
    }

    override fun login(prefectureType: PrefectureType, job: String?): Single<Boolean> {
        return Single.create { result ->
            auth.currentUser?.getIdToken(false)?.result?.token?.let { token ->
                val requestBody = LoginRequestBody(prefectureType.rawValue, if (job?.isEmpty() == true) null else job)
                api.login("Bearer $token", requestBody)
                    .subscribeOn(Schedulers.io())
                    .subscribeBy (
                        onSuccess = {
                            result.onSuccess(true)
                        },
                        onError = { e ->
                            result.onError(e)
                        }
                )
            }?: result.onError(TraceCovid19Exception(Auth))
        }
    }
}