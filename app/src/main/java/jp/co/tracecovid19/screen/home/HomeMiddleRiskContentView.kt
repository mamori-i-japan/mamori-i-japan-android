package jp.co.tracecovid19.screen.home

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import jp.co.tracecovid19.R
import kotlinx.android.synthetic.main.view_home_risk_low.view.shareButton
import kotlinx.android.synthetic.main.view_home_risk_middle.view.*

class HomeMiddleRiskContentView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {

    interface HomeMiddleRiskContentViewEventListener {
        fun onClickNotification()
        fun onClickShareButton()
    }

    var listener: HomeMiddleRiskContentViewEventListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_home_risk_middle, this, true)

        notificationButton.setOnClickListener {
            listener?.onClickNotification()
        }

        shareButton.setOnClickListener {
            listener?.onClickShareButton()
        }
    }

    fun updateContent() {
    }
}
