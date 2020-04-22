package jp.co.tracecovid19.streetpass

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Context
import android.os.Build
import android.util.Log
import com.google.gson.Gson

class Work(
    var device: BluetoothDevice,
    var connectable: ConnectablePeripheral,
    private val onWorkTimeoutListener: OnWorkTimeoutListener
) : Comparable<Work> {

    companion object {
        private const val TAG = "Work"
    }

    var gatt: BluetoothGatt? = null

    var timeStamp: Long = System.currentTimeMillis()
    var finished = false
    var timeout: Long = 0
    var checklist = WorkCheckList()

    val timeoutRunnable: Runnable = Runnable {
        onWorkTimeoutListener.onWorkTimeout(this)
    }

    override fun compareTo(other: Work): Int {
        return timeStamp.compareTo(other.timeStamp)
    }

    fun isSafelyCompleted(): Boolean {
        return (checklist.connected.status && checklist.readCharacteristic.status && checklist.disconnected.status) || checklist.skipped.status
    }

    fun isCriticalsCompleted(): Boolean {
        return (checklist.connected.status && checklist.readCharacteristic.status) || checklist.skipped.status
    }

    fun startWork(
        context: Context,
        gattCallback: StreetPassWorker.CentralGattCallback
    ) {

        gatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
        } else {
            device.connectGatt(context, false, gattCallback)
        }

        if (gatt == null) {
            Log.e(TAG, "Unable to connect to ${device.address}")
        }
    }

    inner class WorkCheckList {
        var started = Check()
        var connected = Check()
        var mtuChanged = Check()
        var readCharacteristic = Check()
        var writeCharacteristic = Check()
        var disconnected = Check()
        var skipped = Check()

        override fun toString(): String {
            return Gson().toJson(this)
        }
    }

    inner class Check {
        var status = false
        var timePerformed: Long = 0
    }

    interface OnWorkTimeoutListener {
        fun onWorkTimeout(work: Work)
    }
}
