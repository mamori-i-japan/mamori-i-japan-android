package jp.mamori_i.app.screen.home

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import jp.mamori_i.app.R
import jp.mamori_i.app.screen.home.HomeStatus.HomeStatusType.*
import kotlinx.android.synthetic.main.view_home_card.view.*
import kotlinx.android.synthetic.main.view_home_card_usual.view.*

class HomeCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RelativeLayout(context, attrs, defStyle) {

    interface HomeCardViewEventListener {
        fun onClickDeepContactCountArea()
    }

    inner class UsualCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RelativeLayout(context, attrs, defStyle) {
        init {
            LayoutInflater.from(context).inflate(R.layout.view_home_card_usual, this, true)
            deepContactCountAreaView.setOnClickListener {
                listener?.onClickDeepContactCountArea()
            }
        }

        fun updateContent(main: String, sub: String?, count: Int) {
            mainMessageTextView.text = main
            subMessageTextView.text = sub?:""
            subMessageTextView.visibility = sub?.let { View.VISIBLE }?: View.GONE
            contactCountTextView.text = count.toString()
        }
    }

    inner class DeepContactCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RelativeLayout(context, attrs, defStyle) {
        init {
            LayoutInflater.from(context).inflate(R.layout.view_home_card_deep_contact, this, true)
            deepContactCountAreaView.setOnClickListener {
                listener?.onClickDeepContactCountArea()
            }
        }
    }

    inner class PositiveCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RelativeLayout(context, attrs, defStyle) {
        init {
            LayoutInflater.from(context).inflate(R.layout.view_home_card_positive, this, true)
        }
    }

    private var listener: HomeCardViewEventListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_home_card, this, true)
    }

    fun setCardViewEventListener(listener: HomeCardViewEventListener) {
        this.listener = listener
    }

    fun updateContent(status: HomeStatus) {
        // 出しわけ
        cardContentContainerView.removeAllViews()
        val contentView = when (status.statusType) {
            Usual -> {
                UsualCardView(context).apply {
                    updateContent(
                        "一緒に日本を守ってくれて\nありがとうございます",
                        "引き続き接触を控えましょう",
                        status.deepContactCount)
                }
            }
            SemiUsual -> {
                UsualCardView(context).apply {
                    updateContent(
                        "接触を減らすために\n周りに協力してもらえることは\nありませんか",
                        null,
                        status.deepContactCount)
                }
            }
            DeepContact -> DeepContactCardView(context)
            Positive -> PositiveCardView(context)
        }
        cardContentContainerView.addView(contentView)

        // 共通
        lastUpdateTextView.text = "最終更新 : ${status.updateDatetimeString}"
        contactCountTextView.text = (status.deepContactCount).toString()
    }
}
