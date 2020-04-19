package jp.co.tracecovid19.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import jp.co.tracecovid19.R
import kotlinx.android.synthetic.main.ui_fetch_error_view.view.*


class FetchErrorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RelativeLayout(context, attrs, defStyle) {

    init {
        LayoutInflater.from(context).inflate(R.layout.ui_fetch_error_view, this, true)
        containerView.visibility = GONE
    }

    fun show(message: String?, onReload: (() -> Unit)? = null) {
        errorView.visibility = VISIBLE
        messageTextView.text = message
        reloadButton.setOnClickListener {
            onReload?.invoke()
            errorView.visibility = GONE
        }
        containerView.visibility = VISIBLE
    }

    fun hide() {
        messageTextView.text = ""
        reloadButton.setOnClickListener(null)
        containerView.visibility = GONE
    }
}
