package jp.co.tracecovid19.idmanager

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.co.tracecovid19.data.model.TempUserId
import jp.co.tracecovid19.data.repository.trase.TraceRepository

// TODO: テストを意識してinterfaceを作るかどうか
class TempIdManager(
    private val traceRepository: TraceRepository,
    private val disposable: CompositeDisposable
) {

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
        if (canNeedFetch(currentTime)) {
            traceRepository.updateTempIds()
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                    onSuccess = {}
                )
                .addTo(disposable)
        }
    }

    /**
     * フェッチしてTempIdの取得が必要かどうかを返却する
     *
     * @return フェッチの必要可否
     */
    private suspend fun canNeedFetch(currentTime: Long): Boolean {
        // TODO: ここのハードコードは設定とかに出していたほうがいいかも
        return (traceRepository.availableTempUserIdCount(currentTime) <= 3)
    }
}