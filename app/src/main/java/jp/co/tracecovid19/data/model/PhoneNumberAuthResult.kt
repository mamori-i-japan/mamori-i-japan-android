package jp.co.tracecovid19.data.model

data class PhoneNumberAuthResult(val verificationId: String?) {
    val didSmsSend = verificationId != null
}