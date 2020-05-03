package jp.mamori_i.app.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.os.Handler
import android.os.ParcelUuid
import android.util.Log
import jp.mamori_i.app.logger.DebugLogger
import jp.mamori_i.app.service.BluetoothMonitoringService.Companion.INFINITE_ADVERTISING
import java.util.*


class BLEAdvertiser(serviceUUID: String) {

    companion object {
        private const val TAG = "BLEAdvertiser"
    }

    var isAdvertising = false
    var shouldBeAdvertising = false

    private var advertiser: BluetoothLeAdvertiser? =
        BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser
    private var charLength = 3
    private var callback: AdvertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            super.onStartSuccess(settingsInEffect)
            DebugLogger.log(TAG, "Advertising onStartSuccess")
            DebugLogger.log(TAG, settingsInEffect.toString())
            isAdvertising = true
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)

            val reason: String

            when (errorCode) {
                ADVERTISE_FAILED_ALREADY_STARTED -> {
                    reason = "ADVERTISE_FAILED_ALREADY_STARTED"
                    isAdvertising = true
                }
                ADVERTISE_FAILED_FEATURE_UNSUPPORTED -> {
                    reason = "ADVERTISE_FAILED_FEATURE_UNSUPPORTED"
                    isAdvertising = false
                }
                ADVERTISE_FAILED_INTERNAL_ERROR -> {
                    reason = "ADVERTISE_FAILED_INTERNAL_ERROR"
                    isAdvertising = false
                }
                ADVERTISE_FAILED_TOO_MANY_ADVERTISERS -> {
                    reason = "ADVERTISE_FAILED_TOO_MANY_ADVERTISERS"
                    isAdvertising = false
                }
                ADVERTISE_FAILED_DATA_TOO_LARGE -> {
                    reason = "ADVERTISE_FAILED_DATA_TOO_LARGE"
                    isAdvertising = false
                    charLength--
                }

                else -> {
                    reason = "UNDOCUMENTED"
                }
            }

            DebugLogger.peripheral(TAG, "Advertising onStartFailure: $errorCode - $reason")
        }
    }

    private val pUuid = ParcelUuid(UUID.fromString(serviceUUID))

    private val settings = AdvertiseSettings.Builder()
        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
        .setConnectable(true)
        .setTimeout(0)
        .build()

    private var data: AdvertiseData? = null

    private var handler = Handler()

    private var stopRunnable: Runnable = Runnable {
        DebugLogger.log(TAG, "Advertising stopping as scheduled.")
        stopAdvertising()
    }

    //reference
    //https://code.tutsplus.com/tutorials/how-to-advertise-android-as-a-bluetooth-le-peripheral--cms-25426
    fun startAdvertisingLegacy(timeoutInMillis: Long) {

        val randomUUID = UUID.randomUUID().toString()
        val finalString = randomUUID.substring(randomUUID.length - charLength, randomUUID.length)
        DebugLogger.peripheral(TAG, "Unique string: $finalString")
        val serviceDataByteArray = finalString.toByteArray()

        data = AdvertiseData.Builder()
            .setIncludeDeviceName(false)
            .setIncludeTxPowerLevel(true)
            .addServiceUuid(pUuid)
            .addManufacturerData(1023, serviceDataByteArray)
            .build()

        try {
            DebugLogger.log(TAG, "Start advertising")
            DebugLogger.peripheral(TAG, "+++++ Start advertising")
            advertiser = advertiser ?: BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser
            advertiser?.startAdvertising(settings, data, callback)
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to start advertising legacy: ${e.message}")
        }

        if (!INFINITE_ADVERTISING) {
            handler.removeCallbacksAndMessages(stopRunnable)
            handler.postDelayed(stopRunnable, timeoutInMillis)
        }
    }

    fun startAdvertising(timeoutInMillis: Long) {
        startAdvertisingLegacy(timeoutInMillis)
        shouldBeAdvertising = true
    }

    fun stopAdvertising() {
        try {
            DebugLogger.log(TAG, "stop advertising")
            DebugLogger.peripheral(TAG, "+++++ Stop advertising")
            advertiser?.stopAdvertising(callback)
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to stop advertising: ${e.message}")
        }
        shouldBeAdvertising = false
        handler.removeCallbacksAndMessages(null)
    }
}
