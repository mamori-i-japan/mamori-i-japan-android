package jp.mamori_i.app.screen.trace

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jp.mamori_i.app.R
import jp.mamori_i.app.extension.convertToDateTimeString
import kotlinx.android.synthetic.main.list_item_trace_history_body.view.*
import kotlinx.android.synthetic.main.list_item_trace_history_section.view.*

class TraceHistoryAdapter(val values: MutableList<TraceHistoryListItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ViewType(val rawValue: Int) {
        Section(0),
        Body(1);

        companion object {
            fun create(rawValue: Int?): ViewType {
                values().firstOrNull {
                    it.rawValue == rawValue
                }?.let {
                    return it
                }
                return Body
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ViewType.create(viewType)) {
            ViewType.Section -> {
                SectionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_trace_history_section, parent, false))
            }
            ViewType.Body -> {
                BodyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_trace_history_body, parent, false))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = values[position]

        when (holder) {
            is SectionViewHolder -> {
                holder.dateTextView.text = item.sectionDate
            }
            is BodyViewHolder -> {
                holder.historyTextView.text = item.deepContact?.startTimeString + "〜" + item.deepContact?.endTimeString
            }
        }
    }

    override fun getItemCount(): Int = values.size

    override fun getItemViewType(position: Int): Int {
        return values[position].type.rawValue
    }

    fun updateValues(values: List<TraceHistoryListItem>) {
        this.values.clear()
        this.values.addAll(values)
        notifyDataSetChanged()
    }

    inner class SectionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.dateTextView
    }

    inner class BodyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val historyTextView: TextView = view.historyTextView
    }
}