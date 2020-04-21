package jp.co.tracecovid19.data.storage

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import io.reactivex.Single
import jp.co.tracecovid19.data.model.FirebaseStorageData
import jp.co.tracecovid19.data.model.TraceCovid19Error

class FirebaseStorageServiceImpl(private val storage: FirebaseStorage):
    FirebaseStorageService {

    override fun loadDataIfNeeded(fileName: FirebaseStorageService.FileNameKey, generation: String, activity: Activity): Single<FirebaseStorageData> {
        val pathReference = storage.reference.child(fileName.rawValue)
        return Single.create { result ->
            pathReference.metadata.addOnSuccessListener { metaData ->
                if (metaData.newerThan(generation)) {
                    // 指定されたgenerationよりあたらしい場合はデータを取得しにいく
                    // TODO 許容するファイルサイズ
                    pathReference.getBytes(1024 * 1024).addOnSuccessListener { data ->
                        data?.let {
                            result.onSuccess(FirebaseStorageData(it, metaData.generation ?: "0"))
                        }?: result.onError(TraceCovid19Error.create(FirebaseException("FirebaseStorage Error No Data")))
                    }
                } else {
                    result.onSuccess(
                        FirebaseStorageData(
                            null,
                            metaData.generation ?: "0"
                        )
                    )
                }
            }.addOnFailureListener {
                result.onError(TraceCovid19Error.create(FirebaseException("FirebaseStorage loadGeneration Error")))
            }
        }
    }

    private fun StorageMetadata.newerThan(targetGeneration: String): Boolean {
        return generation?.let { generation ->
            return generation.toLong() > targetGeneration.toLong()
        }?: true // generationがなかったら、新しい扱い
    }
}