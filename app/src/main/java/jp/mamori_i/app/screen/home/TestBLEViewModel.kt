package jp.mamori_i.app.screen.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.mamori_i.app.idmanager.TempIdManager
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class TestBLEViewModel(private val tempIdManager: TempIdManager) : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var checkTempIdJob: Job? = null

    override fun onCleared() {
        job.cancel()
        checkTempIdJob?.cancel()
        super.onCleared()
    }

    private val _tempId: MutableLiveData<String> = MutableLiveData<String>("")
    val tempId: LiveData<String> = _tempId

    fun subscribe() {
        checkTempIdJob?.cancel()
        checkTempIdJob = launch(Dispatchers.IO) {
            while (isActive) {
                val currentTempId = tempIdManager.getTempUserId(System.currentTimeMillis())
                _tempId.postValue(currentTempId.tempId)

                delay(60 * 1000)
            }
        }
    }

    fun unsubscribe() {
        checkTempIdJob?.cancel()
        checkTempIdJob = null
    }
}