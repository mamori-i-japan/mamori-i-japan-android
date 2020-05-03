package jp.mamori_i.app.screen.home

class HomeStatus(val statusType: HomeStatusType,
                 val deepContactCount: Int) {
    enum class HomeStatusType {
        Usual,
        SemiUsual
    }
}