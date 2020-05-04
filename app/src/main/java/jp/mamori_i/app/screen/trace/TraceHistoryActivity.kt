package jp.mamori_i.app.screen.trace

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.mamori_i.app.R
import jp.mamori_i.app.extension.setUpToolBar
import jp.mamori_i.app.screen.home.TestContactViewAdapter
import kotlinx.android.synthetic.main.activity_test_contact_list.*
import kotlinx.android.synthetic.main.activity_trace_history.*
import kotlinx.android.synthetic.main.activity_trace_history.toolBar
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class TraceHistoryActivity: AppCompatActivity() {
    companion object {
        const val KEY = "jp.mamori_i.app.screen.trace.TraceHistoryActivity"
    }

    private val viewModel: TraceHistoryViewModel by viewModel()
    private val disposable: CompositeDisposable by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初期設定
        initialize()
        // viewの初期設定
        setupViews()
        // viewModelとのbind
        bind()
        // ロード
        viewModel.loadListItems()
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

    private fun initialize() {
        setContentView(R.layout.activity_trace_history)
    }

    private fun setupViews() {
        // ツールバー
        setUpToolBar(toolBar, "接触履歴")
        // リスト
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@TraceHistoryActivity)
            adapter = TraceHistoryAdapter(mutableListOf())
        }
    }

    private fun bind() {
        viewModel.listItems
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                if (it.count() > 0) {
                    noDataMessageTextView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                } else {
                    noDataMessageTextView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
                (recyclerView.adapter as? TraceHistoryAdapter)?.updateValues(it)
            }.addTo(disposable)
    }
}