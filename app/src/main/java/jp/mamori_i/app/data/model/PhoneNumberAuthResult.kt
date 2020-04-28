package jp.mamori_i.app.data.model

data class PhoneNumberAuthResult(val verificationId: String?) {
    val didSmsSend = verificationId != null
}