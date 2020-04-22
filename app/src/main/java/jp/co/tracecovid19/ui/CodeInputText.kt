package jp.co.tracecovid19.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import jp.co.tracecovid19.R
import kotlinx.android.synthetic.main.ui_code_input_text.view.*
import kotlinx.android.synthetic.main.ui_code_input_text_item.view.*

class CodeInputText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {

    class CodeInputTextItem @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RelativeLayout(context, attrs, defStyle) {

        init {
            LayoutInflater.from(context).inflate(R.layout.ui_code_input_text_item, this, true)
        }

        internal fun setCode(code: String) {
            textView.text = code
            updateState(false)
        }

        internal fun clear() {
            textView.text = ""
            updateState(true)
        }

        internal fun code(): String {
            return textView.text.toString()
        }

        internal fun updateState(isFocus: Boolean) {
            if (isFocus) {
                focusView.setBackgroundColor(context.resources.getColor(R.color.colorCodeInputFocus, null))
            } else {
                focusView.setBackgroundColor(context.resources.getColor(R.color.colorCodeInputUnFocus, null))
            }
        }
    }

    interface CodeInputTextListener {
        fun inputFinished(code: String)
    }

    private var listener: CodeInputTextListener? = null
    private var num = 6
    private var currentItemIndex = 0
    private var inputItems: MutableList<CodeInputTextItem> = mutableListOf()

    init {
        LayoutInflater.from(context).inflate(R.layout.ui_code_input_text, this, true)
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.CodeInputText, defStyle, 0
        )
        num = a.getInteger(R.styleable.CodeInputText_codeInputText_num, num)
        for (i in 0 until num) {
            val itemView = CodeInputTextItem(context).apply {
                tag = i
                if (i == 0) {
                    updateState(true)
                }
            }
            inputItems.add(itemView)
        }
        inputItems.forEach {
            containerView.addView(it)
        }

        editText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(input: CharSequence?, start: Int, before: Int, count: Int) {
                if (before > 0) {
                    // 文字の削除
                    back()
                } else {
                    // 文字の追加
                    setCode(input?.last().toString())
                }
            }
        })
    }

    fun setListener(listener: CodeInputTextListener) {
        this.listener = listener
    }

    private fun back() {
        if (currentItemIndex == 0) { return }
        // 今のフォーカスを外す
        inputItems.firstOrNull { it.tag == currentItemIndex }?.updateState(false)
        // 前の値をクリアする
        currentItemIndex--
        inputItems.firstOrNull { it.tag == currentItemIndex }?.clear()
    }

    private fun setCode(code: String) {
        val regex = Regex(pattern = "^[0-9]")
        val matched = regex.containsMatchIn(input = code)
        if (!matched) {
            return
        }

        // エラーを消す
        clearError()

        // 値をセット
        inputItems.firstOrNull { it.tag == currentItemIndex }?.setCode(code)

        // 入力対象が最大桁に達しているかを確認
        if (currentItemIndex + 1 < num) {
            // 達していない場合は、次にフォーカス
            currentItemIndex++
            inputItems.firstOrNull { it.tag == currentItemIndex }?.updateState(true)
        } else {
            // 達している場合は、入力完了通知
            listener?.inputFinished(joinedCode())
        }
    }

    private fun joinedCode(): String {
        return inputItems.joinToString("") { it.code() }
    }

    fun clear() {
        inputItems.forEach { it.setCode("") }
        currentItemIndex = 0
        inputItems.firstOrNull { it.tag == currentItemIndex }?.updateState(true)
    }

    fun showError(message: String) {
        clear()
        errorTextView.text = message
    }

    fun clearError() {
        errorTextView.text = ""
    }
}
