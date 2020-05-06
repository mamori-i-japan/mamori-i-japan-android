package jp.mamori_i.app.data.repository.trace

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.Moshi
import io.mockk.mockk
import jp.mamori_i.app.data.api.trace.TraceApiService
import jp.mamori_i.app.data.database.MIJDatabase
import jp.mamori_i.app.data.database.tempuserid.TempUserIdEntity
import jp.mamori_i.app.data.repository.trase.TraceRepository
import jp.mamori_i.app.data.repository.trase.TraceRepositoryImpl
import jp.mamori_i.app.data.storage.FirebaseStorageService
import jp.mamori_i.app.data.storage.LocalCacheService
import jp.mamori_i.app.data.storage.LocalStorageService
import jp.mamori_i.app.extension.convertToUnixTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.isAccessible

class TraceRepositoryUnitTest {

    private lateinit var repository: TraceRepository

    @Before
    fun before() {
        repository = TraceRepositoryImpl(
            mockk<Moshi> {},
            mockk<TraceApiService> {},
            mockk<FirebaseAuth> {},
            mockk<LocalCacheService> {},
            mockk<LocalStorageService> {},
            mockk<FirebaseStorageService> {},
            mockk< FirebaseFirestore> {},
            mockk<MIJDatabase> {}
        )
    }

    @Test
    fun createTempId_date_change_before_test() {
        val result = callCreateTempId("2020/05/01 10:20:30")
        assertNotNull(result)
        assertNotNull(result!!.tempId)
        val format = "yyyy/MM/dd HH:mm:ss"
        assertEquals(result!!.startTime, "2020/05/01 04:00:00".convertToUnixTime(format))
        assertEquals(result!!.expiryTime, "2020/05/02 04:00:00".convertToUnixTime(format))
    }

    @Test
    fun createTempId_date_change_after_test() {
        val result = callCreateTempId("2020/05/02 00:20:30")
        assertNotNull(result)
        assertNotNull(result!!.tempId)
        val format = "yyyy/MM/dd HH:mm:ss"
        assertEquals(result!!.startTime, "2020/05/01 04:00:00".convertToUnixTime(format))
        assertEquals(result!!.expiryTime, "2020/05/02 04:00:00".convertToUnixTime(format))
    }

    private fun callCreateTempId(date: String): TempUserIdEntity? {
        val method = repository::class.declaredFunctions.find { it.name == "createTempId" }

        val currentTime= date.convertToUnixTime("yyyy/MM/dd HH:mm:ss")
        val actual = method?.let {
            it.isAccessible = true
            println(it.toString())
            it.call(repository, currentTime)
        }
        return actual as? TempUserIdEntity
    }
}