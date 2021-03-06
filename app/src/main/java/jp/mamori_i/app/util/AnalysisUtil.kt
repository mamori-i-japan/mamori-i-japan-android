package jp.mamori_i.app.util

import jp.mamori_i.app.data.database.deepcontactuser.DeepContactUserEntity
import jp.mamori_i.app.data.database.tracedata.TraceDataEntity
import jp.mamori_i.app.data.model.TempUserId

class AnalysisUtil {

    companion object {

        /**
         * 陽性者リストとTempIdリストを食わせたら、リストの中に陽性者がいるかどうかを判定する
         * positivePersonList: 陽性者リスト
         * tempIdList : TempIdのリスト
         * @return Boolean : 陽性かどうか
         */
        fun analysisPositive(positivePersonList: List<String>,
                             tempIdList: List<TempUserId>): Boolean {
            return tempIdList.map { it.tempId }
                .firstOrNull { positivePersonList.contains(it)}
                ?.let {
                    true
                }?: false
        }

        /**
         * 陽性者リストと濃厚接触者Entityリストを食わせたら、自身が陽性者と濃厚接触したかどうかを判定する。その際に最新の接触データも返却する。
         * positivePersonList: 陽性者リスト
         * deepContactUserList : 濃厚接触テーブルEntityリスト
         * @return DeepContactUserEntity? : 最後に陽性者と濃厚接触したデータ / null = 陽性者との濃厚接触なし
         */
        fun analysisDeepContactWithPositivePerson(positivePersonList: List<String>,
                                                  deepContactUserList: List<DeepContactUserEntity>): DeepContactUserEntity? {
            return deepContactUserList.sortedBy { it.endTime }
                .reversed()
                .firstOrNull { positivePersonList.contains(it.tempId) }
        }

        /**
         * 同一tempUserIdごとのTraceDataEntityを食わせたら、濃厚接触テーブル用Entityのリストを生成し返却する
         * borderTime: 境界時間。これよりも後の日時のデータがあったら判定をスキップするためnullを返却
         * continuationInterval : 近接状態が継続されていると判定される間隔
         * densityInterval : 濃厚接触と判定される継続時間
         * @return List<DeepContactUserEntity>? : 濃厚接触テーブル用のEntityリスト
         */
        fun analysisDeepContacts(target: List<TraceDataEntity>,
                                 borderTime: Long,
                                 continuationInterval: Long,
                                 densityInterval: Long): List<DeepContactUserEntity>? {
            // 1. まずtimestampをASKでソート // TODO これSQLでやるなら不要
            // NOTE: 接触情報として使用するかどうかの判定(filter)はここで行う
            // 例: RSSIの値が-XX以上だったら
            val sortedTargetList = target.sortedBy { it.timestamp }

            // 2. 最後のデータの日時が基準時間を超えていた場合、nullを返却する
            if (sortedTargetList.last().timestamp > borderTime) return null

            // 3. 次データとのtimeStamp比較リストを作成
            val compareTimes = sortedTargetList.zipWithNext { a, b ->
                b.timestamp - a.timestamp
            }

            // 4. 指定されたcontinuationIntervalより離れている箇所を抽出
            val separateIndexes: MutableList<Int> = mutableListOf()
            compareTimes.forEachIndexed { index, l ->
                if (l > continuationInterval) {
                    separateIndexes.add(index+1)
                }
            }

            // 5. 離れている箇所で分割する
            val separatedTargetList: MutableList<List<TraceDataEntity>> = mutableListOf()
            var from = 0
            separateIndexes.forEach { to ->
                separatedTargetList.add(sortedTargetList.subList(from, to))
                from = to
            }
            separatedTargetList.add(sortedTargetList.subList(from, sortedTargetList.lastIndex+1)) // 最後に残りを追加

            // 6. 分割したリストそれぞれのstartとendの差分が、densityInterval以上であるかどうかで分割。
            // densityInterval以上のリストは濃厚接触配列となる。
            return separatedTargetList.filter { target ->
                (target.last().timestamp - target.first().timestamp) >= densityInterval
                // NOTE: もし今後期間内のrssiを計算に盛り込む場合はここで平均をとって判定することになりそう
            }.map { contact ->
                val first = contact.first()
                val last = contact.last()
                DeepContactUserEntity(
                    first.tempId,
                    first.timestamp,
                    last.timestamp
                )
            }
        }
    }
}