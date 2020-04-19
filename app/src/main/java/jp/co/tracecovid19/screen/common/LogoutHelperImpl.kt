package jp.co.tracecovid19.screen.common

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import jp.co.tracecovid19.data.repository.session.SessionRepository
import jp.co.tracecovid19.data.repository.trase.TraceRepository
import jp.co.tracecovid19.util.BLEUtil


class LogoutHelperImpl(private val context: Context,
                       private val sessionRepository: SessionRepository,
                       private val traceRepository: TraceRepository
): LogoutHelper {

    override suspend fun logout() {
        // なんかログアウト処理いろいろ (データ消すとか、講読解除とか)
        FirebaseAuth.getInstance().signOut()
        traceRepository.deleteAllData()

        BLEUtil.stopBluetoothMonitoringService(context)
    }
}