package jp.mamori_i.app.data.model

data class UserStatus(val statusType: UserStatusType, val deepContactCount: Int?) {
    enum class UserStatusType {
        Usual,
        SemiUsual
    }
}
