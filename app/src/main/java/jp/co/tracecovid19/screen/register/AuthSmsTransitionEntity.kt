package jp.co.tracecovid19.screen.register

import jp.co.tracecovid19.data.model.PrefectureType
import jp.co.tracecovid19.data.model.Profile
import java.io.Serializable

data class AuthSmsTransitionEntity(val verificationId: String, val profile: Profile): Serializable