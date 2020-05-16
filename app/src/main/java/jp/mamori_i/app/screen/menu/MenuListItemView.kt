package jp.mamori_i.app.screen.menu

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import jp.mamori_i.app.R
import kotlinx.android.synthetic.main.list_item_menu.view.*

class MenuListItemView @JvmOverloads constructor(context: Context,
                                                 item: MenuListItem,
                                                 attrs: AttributeSet? = null,
                                                 defStyle: Int = 0) : RelativeLayout(context, attrs, defStyle) {

    enum class MenuListItemType {
        Setting,
        About,
        License,
        Logout, // TODO Debug
        Restart // TODO Debug
    }

    class MenuListItem(val type: MenuListItemType, val title: String, val selectAction: (() -> Unit)? = null)

    init {
        LayoutInflater.from(context).inflate(R.layout.list_item_menu, this, true)
        titleTextView.text = item.title
        selectButton.setOnClickListener {
            item.selectAction?.invoke()
        }
    }
}
