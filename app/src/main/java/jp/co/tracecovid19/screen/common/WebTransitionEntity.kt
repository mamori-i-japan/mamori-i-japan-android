package jp.co.tracecovid19.screen.common

import java.io.Serializable



data class WebTransitionEntity(val urlString: String,
                               val title: String = "",
                               val subTitle: String = "",
                               val zoomEnabled: Boolean = false): Serializable