package jp.mamori_i.app.data.storage

import android.app.Activity
import io.reactivex.Single
import jp.mamori_i.app.data.model.FirebaseStorageData

interface FirebaseStorageService {

    enum class FileNameKey(val rawValue: String) {
        PositivePersonList("positives.json.gz"),
        AppStatus("app_status.json")
    }

    fun loadDataIfNeeded(fileName: FileNameKey, subDirectory: String?, generation: String, activity: Activity): Single<FirebaseStorageData>
}