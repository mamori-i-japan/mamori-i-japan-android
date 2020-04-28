package jp.mamori_i.app.screen.profile

import jp.mamori_i.app.data.model.Profile
import java.io.Serializable

data class InputPrefectureTransitionEntity(val profile: Profile,
                                           val isRegistrationFlow: Boolean): Serializable