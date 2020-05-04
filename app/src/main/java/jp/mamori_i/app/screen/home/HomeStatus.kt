package jp.mamori_i.app.screen.home

import jp.mamori_i.app.extension.convertToDateTimeString

class HomeStatus(val statusType: HomeStatusType,
                 val deepContactCount: Int,
                 private val updateDatetime: Long) {

    val updateDatetimeString = updateDatetime.convertToDateTimeString("MM月dd日HH時")

    enum class HomeStatusType {
        Usual,
        SemiUsual
    }
}