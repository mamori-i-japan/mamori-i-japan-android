package jp.co.tracecovid19.screen.trace

import androidx.lifecycle.ViewModel
import io.reactivex.subjects.PublishSubject
import jp.co.tracecovid19.data.model.DeepContact
import jp.co.tracecovid19.data.repository.trase.TraceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class TraceHistoryViewModel(private val traceRepository: TraceRepository): ViewModel(), CoroutineScope {

    val deepContacts = PublishSubject.create<List<DeepContact>>()

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    fun loadDeepContacts() {
        launch(Dispatchers.IO) {
            deepContacts.onNext(traceRepository.selectAllDeepContactUsers().map { DeepContact.create(it) })
        }
    }
}