package jp.co.tracecovid19.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import jp.co.tracecovid19.logger.DebugLogger
import jp.co.tracecovid19.util.BLEUtil
import java.util.*
import kotlin.collections.ArrayList

class BLEScanner(context: Context, uuid: String, reportDelay: Long) {

    companion object {
        private const val TAG = "BLEScanner"
    }

    private val serviceUUID: String = uuid
    private val reportDelay: Long = reportDelay

    private var scanCallback: ScanCallback? = null
    private var scanner: BluetoothLeScanner? =
        BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner

    fun startScan(scanCallback: ScanCallback) {
        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(UUID.fromString(serviceUUID)))
            .build()

        val filters: ArrayList<ScanFilter> = ArrayList()
        filters.add(filter)

        val settings = ScanSettings.Builder()
            .setReportDelay(reportDelay)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()

        this.scanCallback = scanCallback
        // try to get a scanner if there isn't anything
        scanner = scanner ?: BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
        scanner?.startScan(filters, settings, scanCallback)
    }

    fun flush() {
        scanCallback?.let {
            scanner?.flushPendingScanResults(scanCallback)
        }
    }

    fun stopScan() {

        try {
            if (scanCallback != null && BLEUtil.isBluetoothAvailable()) { //fixed crash if BT if turned off, stop scan will crash.
                scanner?.stopScan(scanCallback)
                DebugLogger.log(TAG, "scanning stopped")
            }
        } catch (e: Throwable) {
            Log.e(
                TAG,
                "unable to stop scanning - callback null or bluetooth off? : ${e.localizedMessage}"
            )
        }
    }
}
