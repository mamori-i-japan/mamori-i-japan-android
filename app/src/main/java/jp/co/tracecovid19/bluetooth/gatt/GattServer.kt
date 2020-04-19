package jp.co.tracecovid19.bluetooth.gatt

import android.bluetooth.*
import android.content.Context
import android.util.Log
import jp.co.tracecovid19.bluetooth.BLE
import jp.co.tracecovid19.bluetooth.BLEPayload
import jp.co.tracecovid19.idmanager.TempIdManager
import jp.co.tracecovid19.logger.DebugLogger
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class GattServer(private val context: Context, serviceUUIDString: String, private val tempIdManager: TempIdManager): CoroutineScope {

    companion object {
        private const val TAG = "GattServer"
    }

    var bluetoothGattServer: BluetoothGattServer? = null

    private val job = Job()

    private var bluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private var serviceUUID: UUID = UUID.fromString(serviceUUIDString)

    private val gattServerCallback = object : BluetoothGattServerCallback() {

        override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
            DebugLogger.log(TAG, "onConnectionStateChange")
            DebugLogger.peripheral(TAG, "onConnectionStateChange")
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    DebugLogger.log(TAG, "${device?.address} Connected to local GATT server")
                    DebugLogger.peripheral(
                        TAG,
                        "${device?.name}(${device?.address}) Connected to local GATT server."
                    )
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    DebugLogger.log(TAG, "${device?.address} Disconnected from local GATT server.")
                    DebugLogger.peripheral(
                        TAG,
                        "${device?.name}(${device?.address}) Disconnected from local GATT server."
                    )
                }

                else -> {
                    DebugLogger.log(TAG, "Connection status: $newState - ${device?.address}")
                }
            }
        }

        //acting as peripheral
        override fun onCharacteristicReadRequest(
            device: BluetoothDevice?,
            requestId: Int,
            offset: Int,
            characteristic: BluetoothGattCharacteristic?
        ) {
            device?.let {

                DebugLogger.log(TAG, "onCharacteristicReadRequest from ${it.address}")
                DebugLogger.peripheral(TAG, "read request from ${it.name}(${it.address})")

                if (BLE.supportsCharUUID(characteristic?.uuid)) {
                    characteristic?.uuid?.let { _ ->

                        runBlocking (Dispatchers.IO) {
                            val currentTime = System.currentTimeMillis()
                            val myTempId = tempIdManager.getTempUserId(currentTime)
                            val value = BLEPayload(myTempId.tempId).getPayload()
                            DebugLogger.peripheral(
                                TAG,
                                "read response data ${String(value, Charsets.UTF_8)}"
                            )
                            bluetoothGattServer?.sendResponse(
                                it,
                                requestId,
                                BluetoothGatt.GATT_SUCCESS,
                                0,
                                value
                            )

                            // tempIdを必要があれば更新
                            tempIdManager.updateTempUserIdIfNeeded(currentTime)
                        }
                    }
                } else {
                    DebugLogger.log(TAG, "unsupported characteristic UUID from ${it.address}")
                    bluetoothGattServer?.sendResponse(
                        device, requestId,
                        BluetoothGatt.GATT_FAILURE, 0, null
                    )
                }
            } ?: run {
                Log.w(TAG, "No device")
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    fun startServer(): Boolean {

        bluetoothGattServer = bluetoothManager.openGattServer(context, gattServerCallback)

        bluetoothGattServer?.let {
            it.clearServices()
            return true
        }
        return false
    }

    fun addService(service: GattService) {
        bluetoothGattServer?.addService(service.gattService)
    }

    fun stop() {
        try {
            bluetoothGattServer?.clearServices()
            bluetoothGattServer?.close()
            job.cancel()
        } catch (e: Throwable) {
            Log.e(TAG, "GATT server can't be closed elegantly ${e.localizedMessage}")
        }
    }
}
