package jp.mamori_i.app.idmanager

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.mamori_i.app.data.model.TempUserId
import jp.mamori_i.app.data.repository.trase.TraceRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// TODO: テストを意識してinterfaceを作るかどうか
class TempIdManager(
    private val traceRepository: TraceRepository
) {

    suspend fun getTempUserId(currentTime: Long): TempUserId {
        return TempUserId.create(traceRepository.getTempUserId(currentTime))
    }
}