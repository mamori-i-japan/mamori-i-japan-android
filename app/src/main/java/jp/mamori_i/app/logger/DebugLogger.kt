package jp.mamori_i.app.logger
import android.util.Log
import io.reactivex.subjects.PublishSubject
import java.text.SimpleDateFormat
import java.util.*

class DebugLogger {
    companion object {
        val logString = PublishSubject.create<String>()

        fun log(tag: String, message: String) {
            Log.d(tag, message)
            //logString.onNext(message)
        }

        fun central(tag: String, message: String) {
            val log = "[Central] ${now()} $message"
            Log.i(tag, log)
            logString.onNext(log)
        }

        fun service(tag: String, message: String) {
            val log = "[Service] ${now()} $message"
            Log.i(tag, log)
            logString.onNext(log)
        }

        fun peripheral(tag: String, message: String) {
            val log = "[Peripheral] ${now()} $message"
            Log.i(tag, log)
            logString.onNext(log)
        }

        private fun now() : String {
            val date = Date()
            val saf = SimpleDateFormat("HH:mm:ss")
            saf.timeZone = TimeZone.getTimeZone("Asia/Tokyo")
            return saf.format(date)
        }
    }
}