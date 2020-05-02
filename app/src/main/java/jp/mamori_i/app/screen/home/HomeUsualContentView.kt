package jp.mamori_i.app.screen.home

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import jp.mamori_i.app.R
import kotlinx.android.synthetic.main.view_home_content_usual.view.*

class HomeUsualContentView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {

    interface HomeUsualContentViewEventListener {
        fun onClickStayHomeButton()
        fun onClickHygieneButton()
        fun onClickContactButton()
        fun onClickShareButton()
    }

    var listener: HomeUsualContentViewEventListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_home_content_usual, this, true)
        stayHomeButton.setOnClickListener {
            listener?.onClickStayHomeButton()
        }
        hygieneButton.setOnClickListener {
            listener?.onClickHygieneButton()
        }
        contactButton.setOnClickListener {
            listener?.onClickContactButton()
        }
        shareButton.setOnClickListener {
            listener?.onClickShareButton()
        }
    }
}
