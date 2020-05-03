package jp.mamori_i.app.idmanager

import jp.mamori_i.app.data.model.TempUserId
import jp.mamori_i.app.data.repository.trase.TraceRepository

class TempIdManager(
    private val traceRepository: TraceRepository
) {

    suspend fun getTempUserId(currentTime: Long): TempUserId {
        return TempUserId.create(traceRepository.getTempUserId(currentTime))
    }
}