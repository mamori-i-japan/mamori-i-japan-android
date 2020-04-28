package jp.mamori_i.app.screen.common

import java.io.Serializable



data class WebTransitionEntity(val urlString: String,
                               val title: String = "",
                               val subTitle: String = "",
                               val zoomEnabled: Boolean = false): Serializable