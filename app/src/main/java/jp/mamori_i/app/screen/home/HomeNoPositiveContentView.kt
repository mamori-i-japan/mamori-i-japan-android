package jp.mamori_i.app.screen.home

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import jp.mamori_i.app.R
import kotlinx.android.synthetic.main.view_home_content_no_positive.view.*

class HomeNoPositiveContentView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {

    interface HomeNoPositiveContentViewEventListener {
        fun onClickStayHomeButton()
        fun onClickHygieneButton()
        fun onClickContactButton()
        fun onClickPositiveReport()
        fun onClickShareButton()
    }

    var listener: HomeNoPositiveContentViewEventListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_home_content_no_positive, this, true)
        stayHomeButton.setOnClickListener {
            listener?.onClickStayHomeButton()
        }
        hygieneButton.setOnClickListener {
            listener?.onClickHygieneButton()
        }
        contactButton.setOnClickListener {
            listener?.onClickContactButton()
        }
        reportButton.setOnClickListener {
            listener?.onClickPositiveReport()
        }
        shareButton.setOnClickListener {
            listener?.onClickShareButton()
        }
    }
}
