package jp.co.tracecovid19.screen.trace

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jp.co.tracecovid19.R
import jp.co.tracecovid19.data.model.DeepContact
import jp.co.tracecovid19.extension.convertToDateTimeString
import kotlinx.android.synthetic.main.list_item_trace_history.view.*

class TraceHistoryAdapter(val values: MutableList<DeepContact>) : RecyclerView.Adapter<TraceHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_trace_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        holder.historyTextView.text = item.tempId + "\n" + item.startTime.convertToDateTimeString("yyyy/MM/dd HH:mm") + "〜" + item.endTime.convertToDateTimeString("yyyy/MM/dd HH:mm")

        with(holder.view) {
            tag = item
        }
    }

    override fun getItemCount(): Int = values.size

    fun updateValues(values: List<DeepContact>) {
        this.values.clear()
        this.values.addAll(values)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val historyTextView: TextView = view.historyTextView
    }
}