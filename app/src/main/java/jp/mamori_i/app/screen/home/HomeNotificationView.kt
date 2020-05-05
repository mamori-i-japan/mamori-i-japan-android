package jp.mamori_i.app.screen.home

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import jp.mamori_i.app.R
import jp.mamori_i.app.data.model.OrganizationNotice
import kotlinx.android.synthetic.main.view_home_notification.view.*

class HomeNotificationView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {

    interface HomeNotificationViewEventListener {
        fun onClickNotificationButton()
    }

    var listener: HomeNotificationViewEventListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_home_notification, this, true)
        notificationButton.setOnClickListener {
            listener?.onClickNotificationButton()
        }
    }

    fun updateContent(organizationNotice: OrganizationNotice) {
        updateAtTextView.text = organizationNotice.updatedAtString()
    }
}
