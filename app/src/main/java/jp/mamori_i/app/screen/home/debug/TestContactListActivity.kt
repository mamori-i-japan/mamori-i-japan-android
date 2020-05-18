package jp.mamori_i.app.screen.home.debug

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.mamori_i.app.R
import jp.mamori_i.app.extension.setUpToolBar
import jp.mamori_i.app.util.BLEUtil
import kotlinx.android.synthetic.main.activity_test_contact_list.*
import kotlinx.android.synthetic.main.test_contact_row_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext


class TestContactListActivity: AppCompatActivity(), CoroutineScope {
    companion object {
        const val TAG = "TestContactListActivity"
        const val KEY = "jp.mamori_i.app.screen.home.debug.TestContactListActivity"
    }

    private val viewModel: TestContactListViewModel by inject()
    private val disposable: CompositeDisposable by inject()

    private val contactList: MutableList<TestContactModel> = mutableListOf()

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初期設定
        initialize()
        // viewの初期設定
        setupViews()
        // viewModelとのbind
        bind()
    }

    override fun onResume() {
        super.onResume()
        viewModel.analyzeDeepContact()
        viewModel.checkDeepContactWithPositivePerson(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun initialize() {
        setContentView(R.layout.activity_test_contact_list)

        contact_list.also {
            it.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
            it.adapter =
                TestContactViewAdapter(
                    this,
                    contactList
                )
            it.layoutManager = LinearLayoutManager(this)
        }
    }

    private fun setupViews() {
        setUpToolBar(toolBar, "濃厚接触リスト(テスト用)")

        refreshButton.setOnClickListener {
            checkResultText.text = ""
            viewModel.checkDeepContactWithPositivePerson(this)
        }
    }

    private fun bind() {
        viewModel.deepContactUserModels.observe(this, Observer {
            contactList.clear()
            contactList.addAll(it)
            contact_list.adapter?.notifyDataSetChanged()
        })

        viewModel.checkResult
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { result ->
                checkResultText.text = "陽性者(${result.tempId})と\n" +
                        "${BLEUtil.getDate(result.startTime, "yyyy/MM/dd HH:mm:ss")} ~ ${BLEUtil.getDate(result.endTime, "yyyy/MM/dd HH:mm:ss")}" +
                        "\nに濃厚接触の疑いがあります。"
            }
            .addTo(disposable)

        viewModel.checkResultNone
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { _ ->
                checkResultText.text = "陽性者と濃厚接触の疑いはありません。"
            }
            .addTo(disposable)
    }
}

class TestContactViewAdapter(private val context: Context, private val contactList: List<TestContactModel>) : RecyclerView.Adapter<TestContactViewAdapter.TestContactViewHolder>() {

    class TestContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tempIdTextView: TextView = view.tempIdTextView
        val startTimeTextView: TextView = view.startTimeTextView
        val endTimeTextView: TextView = view.endTimeTextViewr
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestContactViewHolder =
        TestContactViewHolder(
            LayoutInflater.from(context).inflate(R.layout.test_contact_row_item, parent, false)
        )

    override fun getItemCount(): Int = contactList.size

    override fun onBindViewHolder(holder: TestContactViewHolder, position: Int) {
        val model = contactList[position]
        holder.tempIdTextView.text = model.tempId
        holder.startTimeTextView.text = BLEUtil.getDate(model.startTime, "yyyy/MM/dd HH:mm:ss")
        holder.endTimeTextView.text = BLEUtil.getDate(model.endTime, "yyyy/MM/dd HH:mm:ss")
    }
}