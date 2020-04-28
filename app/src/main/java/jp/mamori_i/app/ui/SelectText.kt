package jp.mamori_i.app.ui

import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import jp.mamori_i.app.R
import kotlinx.android.synthetic.main.ui_select_text.view.*

class SelectText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {

    interface SelectTextItem {
        fun description(): String
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.ui_select_text, this, true)
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.SelectText, defStyle, 0
        )
        a.getString(R.styleable.SelectText_selectText_hint)?.let {
            editText.hint = it
        }
    }

    private lateinit var selectDataSource: Array<out SelectTextItem>
    private var initValue: SelectTextItem? = null

    private fun selectDisplaySource(): Array<String> {
        return selectDataSource.map{ it.description() }.toTypedArray()
    }
    private var selectedItem: SelectTextItem? = null

    fun setSelectDataSource(dataSource: Array<out SelectTextItem>, initValue: SelectTextItem? = null) {
        selectDataSource = dataSource
        this.initValue = initValue

        selectButton.setOnClickListener {
            showPicker()
        }
    }

    fun setItem(item: SelectTextItem) {
        editText.setText(item.description(), TextView.BufferType.NORMAL)
        selectedItem = item
    }

    fun selectItem(): SelectTextItem? {
        return selectedItem
    }

    fun showPicker() {
        val picker = NumberPicker(this.context)
        picker.displayedValues = selectDisplaySource()
        picker.minValue = 0
        picker.maxValue = selectDataSource.count() - 1
        picker.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        initValue?.let { initValue ->
            picker.value = selectDataSource.indexOf(initValue)
        }
        val builder = AlertDialog.Builder(this.context)
        builder.setView(picker)
        builder.setPositiveButton("決定") { _, _ ->
            val selectedIndex = picker.value
            if (selectedIndex < selectDataSource.count()) {
                setItem(selectDataSource[selectedIndex])
            }
        }
        builder.create().show()
    }
}
