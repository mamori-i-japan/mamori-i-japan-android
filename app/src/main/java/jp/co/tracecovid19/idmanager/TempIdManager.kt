package jp.co.tracecovid19.idmanager

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.co.tracecovid19.data.model.TempUserId
import jp.co.tracecovid19.data.repository.trase.TraceRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// TODO: テストを意識してinterfaceを作るかどうか
class TempIdManager(
    private val traceRepository: TraceRepository,
    private val disposable: CompositeDisposable
) {

    private val mutex = Mutex()
    private var requesting = false

    suspend fun getTempUserId(currentTime: Long): TempUserId {
        val tempUserIds = traceRepository.getTempUserId(currentTime)
        val targetTempUserId = if (tempUserIds.isNotEmpty()) {
            tempUserIds.first()
        } else {
            traceRepository.getLatestTempUserId()
        }
        return TempUserId.create(targetTempUserId)
    }

    suspend fun updateTempUserIdIfNeeded(currentTime: Long) {
        mutex.withLock {
            if (requesting || canNeedFetch(currentTime)) {
                return
            }
            requesting = true
        }

        traceRepository.updateTempIds()
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = { requesting = false },
                onError = { requesting = false }
            )
            .addTo(disposable)
    }

    /**
     * フェッチしてTempIdの取得が必要かどうかを返却する
     *
     * @return フェッチの必要可否
     */
    private suspend fun canNeedFetch(currentTime: Long): Boolean {
        // TODO: ここのハードコードは設定とかに出していたほうがいいかも
        return (traceRepository.availableTempUserIdCount(currentTime) <= 2)
    }
}