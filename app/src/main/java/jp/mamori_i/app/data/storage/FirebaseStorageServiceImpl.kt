package jp.mamori_i.app.data.storage

import android.app.Activity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import io.reactivex.Single
import jp.mamori_i.app.data.exception.MIJException
import jp.mamori_i.app.data.exception.MIJException.Reason.*
import jp.mamori_i.app.data.model.FirebaseStorageData

class FirebaseStorageServiceImpl(private val storage: FirebaseStorage):
    FirebaseStorageService {

    override fun loadDataIfNeeded(fileName: FirebaseStorageService.FileNameKey, subDirectory: String?, generation: String, activity: Activity): Single<FirebaseStorageData> {
        val pathReference = subDirectory?.let {
            storage.reference.child(it).child(fileName.rawValue)
        }?: storage.reference.child(fileName.rawValue)
        return Single.create { result ->
            pathReference.metadata.addOnSuccessListener { metaData ->
                if (metaData.newerThan(generation)) {
                    // 指定されたgenerationよりあたらしい場合はデータを取得しにいく
                    // TODO 許容するファイルサイズ
                    pathReference.getBytes(1024 * 1024).addOnSuccessListener { data ->
                        data?.let {
                            result.onSuccess(FirebaseStorageData(it, metaData.generation ?: "0"))
                        }?: result.onError(MIJException(Other))
                    }
                } else {
                    result.onSuccess(FirebaseStorageData(null, metaData.generation ?: "0"))
                }
            }.addOnFailureListener { e ->
                result.onError(e)
            }
        }
    }

    private fun StorageMetadata.newerThan(targetGeneration: String): Boolean {
        return generation?.let { generation ->
            return generation.toLong() > targetGeneration.toLong()
        }?: true // generationがなかったら、新しい扱い
    }
}