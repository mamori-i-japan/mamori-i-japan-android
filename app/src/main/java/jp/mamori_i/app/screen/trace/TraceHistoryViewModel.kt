package jp.mamori_i.app.screen.trace

import androidx.lifecycle.ViewModel
import io.reactivex.subjects.PublishSubject
import jp.mamori_i.app.data.database.deepcontactuser.DeepContactUserEntity
import jp.mamori_i.app.data.model.DeepContact
import jp.mamori_i.app.data.repository.trase.TraceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class TraceHistoryViewModel(private val traceRepository: TraceRepository): ViewModel(), CoroutineScope {

    val listItems = PublishSubject.create<List<TraceHistoryListItem>>()

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    fun loadListItems() {
        launch(Dispatchers.IO) {
            // まず濃厚接触履歴を全件取得 (DeepContactに変換・降順ソート)
            val deepContacts = listOf(DeepContactUserEntity("hoge",1,2),
                DeepContactUserEntity("hoge1",1,2),
                DeepContactUserEntity("hoge2",200000000,2))
                .map { DeepContact.create(it) }
                .sortedBy { it.startTime }

                /*traceRepository.selectAllDeepContactUsers()
                .map { DeepContact.create(it) }
                .sortedBy { it.startTime }*/
            // 年月日ごとにセパレートする
            val separatedDeepContacts = deepContacts.groupBy{ it.startDateString }
            // 年月日を１セクションとし、ListItemを生成する
            val items = mutableListOf<TraceHistoryListItem>()
            separatedDeepContacts.forEach { (date, contacts) ->
                items.add(TraceHistoryListItem(TraceHistoryAdapter.ViewType.Section, date, null))
                contacts.forEach {
                    items.add(TraceHistoryListItem(TraceHistoryAdapter.ViewType.Body, null, it))
                }
            }

            listItems.onNext(items)
        }
    }
}