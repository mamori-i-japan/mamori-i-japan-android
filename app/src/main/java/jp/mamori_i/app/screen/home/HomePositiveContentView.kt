package jp.mamori_i.app.screen.home

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import jp.mamori_i.app.R
import kotlinx.android.synthetic.main.view_home_content_no_positive.view.*

class HomePositiveContentView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {

    interface HomePositiveContentViewEventListener {
        fun onClickShareButton()
    }

    var listener: HomePositiveContentViewEventListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_home_content_positive, this, true)
        shareButton.setOnClickListener {
            listener?.onClickShareButton()
        }
    }
}
