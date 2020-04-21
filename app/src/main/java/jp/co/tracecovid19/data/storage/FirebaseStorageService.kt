package jp.co.tracecovid19.data.storage

import android.app.Activity
import io.reactivex.Single
import jp.co.tracecovid19.data.model.FirebaseStorageData

interface FirebaseStorageService {

    enum class FileNameKey(val rawValue: String) {
        PositivePersonList("positives.json.gz"),
        AppStatus("app_status.json")
    }

    fun loadDataIfNeeded(fileName: FileNameKey, generation: String, activity: Activity): Single<FirebaseStorageData>
}