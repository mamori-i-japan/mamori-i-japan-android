package jp.co.tracecovid19.screen.home

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.co.tracecovid19.R
import jp.co.tracecovid19.data.repository.session.SessionRepository
import jp.co.tracecovid19.data.repository.trase.TraceRepository
import jp.co.tracecovid19.extension.setUpToolBar
import jp.co.tracecovid19.idmanager.TempIdManager
import jp.co.tracecovid19.util.BLEUtil
import kotlinx.android.synthetic.main.activity_b_l_e.*
import kotlinx.android.synthetic.main.ble_contact_row_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class BLEActivity : AppCompatActivity(), CoroutineScope {

    companion object {
        const val TAG = "BLEActivity"
        const val KEY = "jp.co.tracecovid19.screen.home.BLEActivity"
    }

    private val sessionRepository: SessionRepository by inject()
    private val traceRepository: TraceRepository by inject()
    private val tempIdManager: TempIdManager by inject()

    private val contactList: MutableList<ContactModel> = mutableListOf()

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

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun initialize() {
        setContentView(R.layout.activity_b_l_e)

        if (initBluetoothAdapter() && initLocationManager()) {
            BLEUtil.startBluetoothMonitoringService(this)
        } else {
            finish()
        }
        contact_list.also {
            it.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
            it.adapter = ContactViewAdapter(this, contactList)
            it.layoutManager = LinearLayoutManager(this)
        }
    }

    private fun setupViews() {
        setUpToolBar(toolBar, "テスト用画面")

        launch (Dispatchers.IO) {
            val tempId = tempIdManager.getTempUserId(System.currentTimeMillis())
            launch {
                currentIdText.text = tempId.tempId
                startText.text = tempId.validFrom
                endText.text = tempId.validTo
            }
        }

        logButton.setOnClickListener {
            val intent = Intent(this, TestBLEActivity::class.java)
            this.startActivity(intent)
        }

        positiveCheckButton.setOnClickListener {
            val intent = Intent(this, TestPositiveCheckActivity::class.java)
            this.startActivity(intent)
        }

        contactButton.setOnClickListener {
            val intent = Intent(this, TestContactListActivity::class.java)
            this.startActivity(intent)
        }
    }

    private fun bind() {
        traceRepository.selectAllTraceData().observe(this@BLEActivity, Observer {
            contactList.clear()
            contactList.addAll(it.map { entity ->
                ContactModel(
                    BLEType.CENTRAL,
                    entity.tempId,
                    BLEUtil.getDate(entity.timestamp),
                    entity.rssi,
                    entity.txPower
                )
            })
            contact_list.adapter?.notifyDataSetChanged()
        })
    }

    private fun initBluetoothAdapter(): Boolean {
        val adapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        if (adapter == null) {
            return false
        } else {
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, 1)
                return false
            }
            return true
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun initLocationManager(): Boolean {
        val permissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(
                    this,
                    "BLE用の位置情報を有効にして下さい。",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), 1
                )
                return false
            }
        }
        return true
    }
}

class ContactViewAdapter(private val context: Context, private val contactList: List<ContactModel>) : RecyclerView.Adapter<ContactViewAdapter.ContactViewHolder>() {

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val typeTextView: TextView = view.typeTextView
        val connectedIDTextView: TextView = view.connectedIDTextView
        val registDateTextView: TextView = view.registDateTextView
        val rssiTextView: TextView = view.rssiTextView
        val txPoweTextView: TextView = view.txPoweTextViewr
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder = ContactViewHolder(LayoutInflater.from(context).inflate(R.layout.ble_contact_row_item, parent, false))

    override fun getItemCount(): Int = contactList.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val model = contactList[position]
        holder.typeTextView.text = model.type.toString()
        holder.connectedIDTextView.text = model.connectedID
        holder.registDateTextView.text = model.registDate
        holder.rssiTextView.text = "RSSI: ${model.rssi.toString()}"
        holder.txPoweTextView.text = "TX Power: ${model.txPower.toString()}"
    }
}

data class ContactModel(
    val type: BLEType,
    val connectedID: String,
    val registDate: String,
    val rssi: Int,
    val txPower: Int?) {
}

enum class BLEType {
    PERIPHERAL,
    CENTRAL
}
