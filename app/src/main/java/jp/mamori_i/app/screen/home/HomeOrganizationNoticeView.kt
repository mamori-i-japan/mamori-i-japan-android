package jp.mamori_i.app.screen.home

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import jp.mamori_i.app.R
import jp.mamori_i.app.data.model.OrganizationNotice
import kotlinx.android.synthetic.main.view_home_organization_notice.view.*

class HomeOrganizationNoticeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {

    interface HomeOrganizationNoticeViewEventListener {
        fun onClickNoticeButton(organizationNotice: OrganizationNotice)
    }

    var listener: HomeOrganizationNoticeViewEventListener? = null
    lateinit var organizationNotice: OrganizationNotice

    init {
        LayoutInflater.from(context).inflate(R.layout.view_home_organization_notice, this, true)
        notificationButton.setOnClickListener {
            listener?.onClickNoticeButton(organizationNotice)
        }
    }

    fun updateContent(organizationNotice: OrganizationNotice) {
        this.organizationNotice = organizationNotice
        updateAtTextView.text = organizationNotice.updatedAtString()
    }
}
