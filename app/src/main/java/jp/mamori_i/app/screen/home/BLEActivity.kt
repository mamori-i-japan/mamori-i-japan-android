package jp.mamori_i.app.screen.home

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.mamori_i.app.R
import jp.mamori_i.app.data.repository.trase.TraceRepository
import jp.mamori_i.app.extension.convertToDateTimeString
import jp.mamori_i.app.extension.setUpToolBar
import jp.mamori_i.app.idmanager.TempIdManager
import jp.mamori_i.app.util.BLEUtil
import kotlinx.android.synthetic.main.activity_b_l_e.*
import kotlinx.android.synthetic.main.activity_b_l_e.toolBar
import kotlinx.android.synthetic.main.activity_test_positive_check.*
import kotlinx.android.synthetic.main.ble_contact_row_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import kotlin.coroutines.CoroutineContext

class BLEActivity : AppCompatActivity(), CoroutineScope {

    companion object {
        const val TAG = "BLEActivity"
        const val KEY = "jp.mamori_i.app.screen.home.BLEActivity"
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 222
        private const val REQUEST_ENABLE_BT = 333
    }

    private val traceRepository: TraceRepository by inject()
    private val tempIdManager: TempIdManager by inject()

    private val contactList: MutableList<ContactModel> = mutableListOf()

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager =
            this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

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

        if (enableBluetooth()) {
            setupPermissionsAndSettings()
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
                startText.text = tempId.startTime.convertToDateTimeString("yyyy/MM/dd HH:mm")
                endText.text = tempId.expiryTime.convertToDateTimeString("yyyy/MM/dd HH:mm")
            }
        }

        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        currentIdText.setOnLongClickListener {
            clipboardManager?.setPrimaryClip(ClipData.newPlainText("", currentIdText.text))
            Toast.makeText(this, "コピーしました", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
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
            contactList.addAll(
                it.map { entity ->
                    ContactModel(
                        BLEType.CENTRAL,
                        entity.tempId,
                        BLEUtil.getDate(entity.timestamp),
                        entity.rssi,
                        entity.txPower
                    )
                }.reversed())
            contact_list.adapter?.notifyDataSetChanged()
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_ACCESS_LOCATION -> {
                BLEUtil.startBluetoothMonitoringService(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT) {
            // BLEを許可しようがしまいが、先に進む
            setupPermissionsAndSettings()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun enableBluetooth(): Boolean {
        bluetoothAdapter?.let {
            if (!it.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                return false
            }
            return true
        } ?: run {
            return false
        }
    }

    @AfterPermissionGranted(PERMISSION_REQUEST_ACCESS_LOCATION)
    private fun setupPermissionsAndSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val perms = BLEUtil.getRequiredPermissions()
            if (EasyPermissions.hasPermissions(this, *perms)) {
                BLEUtil.startBluetoothMonitoringService(this)
            } else {
                EasyPermissions.requestPermissions(
                    this, getString(R.string.permission_location_rationale), PERMISSION_REQUEST_ACCESS_LOCATION, *perms)
            }
        }
    }

}

class ContactViewAdapter(private val context: Context, private val contactList: List<ContactModel>) : RecyclerView.Adapter<ContactViewAdapter.ContactViewHolder>() {

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val typeTextView: TextView = view.typeTextView
        val connectedIDTextView: TextView = view.connectedIDTextView
        val registerDateTextView: TextView = view.regisertDateTextView
        val rssiTextView: TextView = view.rssiTextView
        val txPowerTextView: TextView = view.txPoweTextViewr
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder = ContactViewHolder(LayoutInflater.from(context).inflate(R.layout.ble_contact_row_item, parent, false))

    override fun getItemCount(): Int = contactList.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val model = contactList[position]
        holder.typeTextView.text = model.type.toString()
        holder.connectedIDTextView.text = model.connectedID
        holder.registerDateTextView.text = model.registerDate
        holder.rssiTextView.text = "RSSI: ${model.rssi}"
        holder.txPowerTextView.text = "TX Power: ${model.txPower.toString()}"
    }
}

data class ContactModel(
    val type: BLEType,
    val connectedID: String,
    val registerDate: String,
    val rssi: Int,
    val txPower: Int?) {
}

enum class BLEType {
    PERIPHERAL,
    CENTRAL
}
