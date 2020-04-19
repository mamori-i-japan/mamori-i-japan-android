package jp.co.tracecovid19.screen.home

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import jp.co.tracecovid19.R
import kotlinx.android.synthetic.main.view_home_risk_low.view.*

class HomeLowRiskContentView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {

    interface HomeLowRiskContentViewEventListener {
        fun onClickShareButton()
    }

    var listener: HomeLowRiskContentViewEventListener? = null


    init {
        LayoutInflater.from(context).inflate(R.layout.view_home_risk_low, this, true)
        shareButton.setOnClickListener {
            listener?.onClickShareButton()
        }
    }

    fun updateContent() {
    }
}
