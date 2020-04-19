package jp.co.tracecovid19.screen.profile

import jp.co.tracecovid19.data.model.Profile
import java.io.Serializable

data class InputJobTransitionEntity(val profile: Profile,
                                    val isRegistrationFlow: Boolean): Serializable