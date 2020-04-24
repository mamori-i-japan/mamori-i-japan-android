package jp.co.tracecovid19.screen.trace

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import jp.co.tracecovid19.data.model.DeepContact
import jp.co.tracecovid19.data.repository.trase.TraceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class TraceHistoryViewModel(traceRepository: TraceRepository): ViewModel(), CoroutineScope {

    val deepContacts = Transformations.map(traceRepository.selectAllLiveDataDeepContactUsers()) {
        it.map { entity -> DeepContact.create(entity) }
    }

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }
}