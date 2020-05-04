package jp.mamori_i.app.util

import jp.mamori_i.app.data.database.deepcontactuser.DeepContactUserEntity
import jp.mamori_i.app.data.database.tracedata.TraceDataEntity
import jp.mamori_i.app.data.model.TempUserId
import org.junit.Assert.*
import org.junit.Test

class AnalysisUtilTest {

    @Test
    fun analysis_deep_contacts_test_zero() {
        /**
         * <濃厚接触0件>
         * 境界時間 30
         * 継続間隔 5
         * 濃厚判定間隔 20
         * 間隔は最初5以内でばらつかるが、20になる前に一回6以上の間隔をあける
         * 検証値: [0, 1, 3, 6, 10, 11, 17, 20, 23]
         * 期待:
         *  []
         */
        val testData = listOf(
            createTestData(0),
            createTestData(1),
            createTestData(3),
            createTestData(6),
            createTestData(10),
            createTestData(11),
            createTestData(17),
            createTestData(20),
            createTestData(23))

        AnalysisUtil.analysisDeepContacts(testData, 30, 5, 20)?.let { result ->
            // 検出件数
            assertEquals(result.count(), 0)
        }?: assert(false)
    }

    @Test
    fun analysis_deep_contacts_test() {

        var testData: List<TraceDataEntity> = listOf()

        /**
         * <濃厚接触1件 + 最後まで継続>
         * 境界時間 30
         * 継続間隔 5
         * 濃厚判定間隔 20
         * 間隔は5以内でばらつかせ、20以上になるまで継続させる
         * 検証値: [0, 1, 3, 6, 10, 11, 15, 20, 23]
         * 期待:
         *  [(0-23)]
         */
        testData = listOf(
            createTestData(0),
            createTestData(1),
            createTestData(3),
            createTestData(6),
            createTestData(10),
            createTestData(11),
            createTestData(15),
            createTestData(20),
            createTestData(23))

        AnalysisUtil.analysisDeepContacts(testData, 30, 5, 20)?.let { result ->
            // 検出件数
            assertEquals(result.count(), 1)
            // 開始time(1件目)
            assertEquals(result.first().startTime, 0)
            // 終了time(1件目)
            assertEquals(result.first().endTime, 23)
        }?: assert(false)

        /**
         * <濃厚接触1件 + 途切れ>
         * 境界時間 40
         * 継続間隔 5
         * 濃厚判定間隔 20
         * 間隔は最初5以内でばらつかせ、20を超える。その後に一度6以上の間隔をあける
         * 検証値: [0, 1, 3, 6, 10, 11, 15, 20, 23, 30, 33, 34]
         * 期待:
         *  [(0-23)]
         */
        testData = listOf(
            createTestData(0),
            createTestData(1),
            createTestData(3),
            createTestData(6),
            createTestData(10),
            createTestData(11),
            createTestData(15),
            createTestData(20),
            createTestData(23),
            createTestData(30),
            createTestData(33),
            createTestData(34))

        AnalysisUtil.analysisDeepContacts(testData, 40, 5, 20)?.let { result ->
            // 検出件数
            assertEquals(result.count(), 1)
            // 開始time(1件目)
            assertEquals(result.first().startTime, 0)
            // 終了time(1件目)
            assertEquals(result.first().endTime, 23)
        }?: assert(false)

        /**
         * <濃厚接触2件>
         * 境界時間 70
         * 継続間隔 5
         * 濃厚判定間隔 20
         * 間隔は最初5以内でばらつかせ、20を超える。その後に一度6以上の間隔をあけ、再び20以上継続する箇所を作る
         * 検証値: [0, 1, 3, 6, 10, 11, 15, 20, 23, 30, 33, 34, 40, 42, 44, 48, 53, 54, 55, 59, 60]
         * 期待:
         *  [(0-23), (40-60)]
         */
        testData = listOf(
            createTestData(0),
            createTestData(1),
            createTestData(3),
            createTestData(6),
            createTestData(10),
            createTestData(11),
            createTestData(15),
            createTestData(20),
            createTestData(23),
            createTestData(30),
            createTestData(33),
            createTestData(34),
            createTestData(40),
            createTestData(42),
            createTestData(44),
            createTestData(48),
            createTestData(53),
            createTestData(54),
            createTestData(55),
            createTestData(59),
            createTestData(60))

        AnalysisUtil.analysisDeepContacts(testData, 70, 5, 20)?.let { result ->
            // 検出件数
            assertEquals(result.count(), 2)
            // 開始time(1件目)
            assertEquals(result.first().startTime, 0)
            // 終了time(1件目)
            assertEquals(result.first().endTime, 23)
            // 開始time(2件目)
            assertEquals(result.last().startTime, 40)
            // 終了time(2件目)
            assertEquals(result.last().endTime, 60)
        }?: assert(false)

        /**
         * <境界時間を超えている>
         * 境界時間 20
         * 継続間隔 5
         * 濃厚判定間隔 20
         * 最後のデータが境界時間を超えている
         * 検証値: [0, 1, 3, 6, 10, 11, 15, 20, 23]
         * 期待:
         *  null
         */
        testData = listOf(
            createTestData(0),
            createTestData(1),
            createTestData(3),
            createTestData(6),
            createTestData(10),
            createTestData(11),
            createTestData(15),
            createTestData(20),
            createTestData(23))

        AnalysisUtil.analysisDeepContacts(testData, 20, 5, 20)?.let { _ ->
            assert(false)
        }?: assert(true)
    }

    @Test
    fun analysis_positive() {

        var testDataP: List<String> = listOf()
        var testDataT: List<TempUserId> = listOf()

        /**
         * <陽性者なし>
         * 該当するIDが0件
         * 検証値
         *  陽性者リスト: [a, b, c, d]
         *  TempIdリスト: [A, B, C, D]
         * 期待:
         *  false
         */
        testDataP = listOf(
            "a",
            "b",
            "c",
            "d")
        testDataT = listOf(
            createTestDate("A"),
            createTestDate("B"),
            createTestDate("C"),
            createTestDate("D"))

        assertFalse(AnalysisUtil.analysisPositive(testDataP, testDataT))

        /**
         * <陽性者あり>
         * 該当するIDが1件
         * 検証値
         *  陽性者リスト: [a, b, c, d]
         *  TempIdリスト: [A, b, C, D]
         * 期待:
         *  false
         */
        testDataP = listOf(
            "a",
            "b",
            "c",
            "d")
        testDataT = listOf(
            createTestDate("A"),
            createTestDate("b"),
            createTestDate("C"),
            createTestDate("D"))

        assertTrue(AnalysisUtil.analysisPositive(testDataP, testDataT))

        /**
         * <陽性者あり>
         * 該当するIDが2件
         * 検証値
         *  陽性者リスト: [a, b, c, d]
         *  TempIdリスト: [A, b, c, D]
         * 期待:
         *  false
         */
        testDataP = listOf(
            "a",
            "b",
            "c",
            "d")
        testDataT = listOf(
            createTestDate("A"),
            createTestDate("b"),
            createTestDate("c"),
            createTestDate("D"))

        assertTrue(AnalysisUtil.analysisPositive(testDataP, testDataT))
    }

    @Test
    fun analysis_deep_contact_with_positive_person() {

        var testDataP: List<String> = listOf()
        var testDataD: List<DeepContactUserEntity> = listOf()

        /**
         * <陽性者との濃厚接触なし>
         * 該当するIDが0件
         * 検証値
         *  陽性者リスト: [a, b, c, d]
         *  濃厚接触リスト: [(A/0-3), (B/4-7), (C/5-10)]
         * 期待:
         *  null (濃厚接触なし)
         */
        testDataP = listOf(
            "a",
            "b",
            "c",
            "d")
        testDataD = listOf(
            createTestDate("A", 0, 3),
            createTestDate("B", 4, 7),
            createTestDate("C", 5, 10))

       AnalysisUtil.analysisDeepContactWithPositivePerson(testDataP, testDataD)?.let {
           assert(false)
       }?: assert(true)

        /**
         * <陽性者との濃厚接触あり>
         * 該当するIDが1件
         * 検証値
         *  陽性者リスト: [a, b, c, d]
         *  濃厚接触リスト: [(A/0-3), (b/4-7), (C/5-10)]
         * 期待:
         *  b/4-7
         */
        testDataP = listOf(
            "a",
            "b",
            "c",
            "d")
        testDataD = listOf(
            createTestDate("A", 0, 3),
            createTestDate("b", 4, 7),
            createTestDate("C", 5, 10))

        AnalysisUtil.analysisDeepContactWithPositivePerson(testDataP, testDataD)?.let { result ->
            assertEquals(result.tempId, "b")
            assertEquals(result.startTime, 4)
            assertEquals(result.endTime, 7)
        }?: assert(false)

        /**
         * <陽性者との濃厚接触あり>
         * 該当するIDが2件, 1人目開始 < 1人目終了 < 2人目開始 < 2人目終了
         * 検証値
         *  陽性者リスト: [a, b, c, d]
         *  濃厚接触リスト: [(A/0-3), (b/4-7), (c/8-10)]
         * 期待:
         *  c/8-10
         */
        testDataP = listOf(
            "a",
            "b",
            "c",
            "d")
        testDataD = listOf(
            createTestDate("A", 0, 3),
            createTestDate("b", 4, 7),
            createTestDate("c", 8, 10))

        AnalysisUtil.analysisDeepContactWithPositivePerson(testDataP, testDataD)?.let { result ->
            assertEquals(result.tempId, "c")
            assertEquals(result.startTime, 8)
            assertEquals(result.endTime, 10)
        }?: assert(false)

        /**
         * <陽性者との濃厚接触あり>
         * 該当するIDが2件, 1人目開始 < 2人目開始 < 1人目終了 < 2人目終了
         * 検証値
         *  陽性者リスト: [a, b, c, d]
         *  濃厚接触リスト: [(A/0-3), (b/4-7), (c/5-10)]
         * 期待:
         *  c/5-10
         */
        testDataP = listOf(
            "a",
            "b",
            "c",
            "d")
        testDataD = listOf(
            createTestDate("A", 0, 3),
            createTestDate("b", 4, 7),
            createTestDate("c", 5, 10))

        AnalysisUtil.analysisDeepContactWithPositivePerson(testDataP, testDataD)?.let { result ->
            assertEquals(result.tempId, "c")
            assertEquals(result.startTime, 5)
            assertEquals(result.endTime, 10)
        }?: assert(false)

        /**
         * <陽性者との濃厚接触あり>
         * 該当するIDが2件, 1人目開始 < 2人目開始 < 2人目終了 < 1人目終了
         * 検証値
         *  陽性者リスト: [a, b, c, d]
         *  濃厚接触リスト: [(A/0-3), (b/4-10), (c/5-9)]
         * 期待:
         *  b/4-10
         */
        testDataP = listOf(
            "a",
            "b",
            "c",
            "d")
        testDataD = listOf(
            createTestDate("A", 0, 3),
            createTestDate("b", 4, 10),
            createTestDate("c", 5, 9))

        AnalysisUtil.analysisDeepContactWithPositivePerson(testDataP, testDataD)?.let { result ->
            assertEquals(result.tempId, "b")
            assertEquals(result.startTime, 4)
            assertEquals(result.endTime, 10)
        }?: assert(false)
    }

    private fun createTestData(timestamp: Long): TraceDataEntity {
        return TraceDataEntity("testId", timestamp, 2, null)
    }

    private fun createTestDate(id: String): TempUserId {
        return TempUserId(id, 0, 0)
    }

    private fun createTestDate(id: String, start: Long, end: Long): DeepContactUserEntity {
        return DeepContactUserEntity(id, start, end)
    }
}