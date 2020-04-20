package jp.co.tracecovid19.streetpass

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanResult.TX_POWER_NOT_PRESENT
import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import jp.co.tracecovid19.bluetooth.BLEScanner
import jp.co.tracecovid19.logger.DebugLogger
import jp.co.tracecovid19.service.BluetoothMonitoringService.Companion.infiniteScanning
import jp.co.tracecovid19.util.BLEUtil

class StreetPassScanner(
    private val context: Context,
    serviceUUIDString: String,
    private val scanDurationInMillis: Long
) {

    companion object {
        private const val TAG = "StreetPassScanner"
    }

    var scannerCount = 0

    private var scanner: BLEScanner = BLEScanner(context, serviceUUIDString, 0)
    private var handler: Handler = Handler()
    private val scanCallback = BleScanCallback()

    fun startScan() {

        DebugLogger.central(TAG, "===== Scanning Started")
        scanner.startScan(scanCallback)
        scannerCount++

        if (!infiniteScanning) {
            handler.postDelayed(
                { stopScan() }
                , scanDurationInMillis)
        }

        DebugLogger.log(TAG, "scanning started")
    }

    fun stopScan() {
        //only stop if scanning was successful - kinda.
        if (scannerCount > 0) {
            scannerCount--
            scanner.stopScan()
            DebugLogger.central(TAG, "===== Scanning Stopped")
        }
    }

    fun isScanning(): Boolean {
        return scannerCount > 0
    }

    inner class BleScanCallback : ScanCallback() {

        private val TAG = "BleScanCallback"

        private fun processScanResult(scanResult: ScanResult?) {
            DebugLogger.log(TAG, "processScanResult")

            scanResult?.let { result ->
                val device = result.device
                val rssi = result.rssi // get RSSI value

                var txPower: Int? = null

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    txPower = result.scanRecord?.txPowerLevel
                    if (txPower == TX_POWER_NOT_PRESENT) {
                        txPower = null
                    }
                }

                val manuData: ByteArray =
                    scanResult.scanRecord?.getManufacturerSpecificData(1023) ?: "N.A".toByteArray()
                val manuString = String(manuData, Charsets.UTF_8)

                val connectable = ConnectablePeripheral(manuString, txPower, rssi)

                DebugLogger.log(TAG, "Scanned: ${manuString} - ${device.address}")
                DebugLogger.central(TAG, "Scanned: ${manuString} - ${device.address}")

                BLEUtil.broadcastDeviceScanned(context, device, connectable)
            }
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            processScanResult(result)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)

            val reason = when (errorCode) {
                SCAN_FAILED_ALREADY_STARTED -> "$errorCode - SCAN_FAILED_ALREADY_STARTED"
                SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> "$errorCode - SCAN_FAILED_APPLICATION_REGISTRATION_FAILED"
                SCAN_FAILED_FEATURE_UNSUPPORTED -> "$errorCode - SCAN_FAILED_FEATURE_UNSUPPORTED"
                SCAN_FAILED_INTERNAL_ERROR -> "$errorCode - SCAN_FAILED_INTERNAL_ERROR"
                else -> {
                    "$errorCode - UNDOCUMENTED"
                }
            }
            Log.e(TAG, "BT Scan failed: $reason")
            if (scannerCount > 0) {
                scannerCount--
            }
        }
    }
}

