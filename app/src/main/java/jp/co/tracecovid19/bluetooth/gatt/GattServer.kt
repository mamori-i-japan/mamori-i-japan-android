package jp.co.tracecovid19.bluetooth.gatt

import android.bluetooth.*
import android.content.Context
import android.util.Log
import jp.co.tracecovid19.bluetooth.BLE
import jp.co.tracecovid19.bluetooth.BLEReadPayload
import jp.co.tracecovid19.bluetooth.BLEWritePayload
import jp.co.tracecovid19.idmanager.TempIdManager
import jp.co.tracecovid19.logger.DebugLogger
import jp.co.tracecovid19.streetpass.BLEType
import jp.co.tracecovid19.streetpass.ConnectionRecord
import jp.co.tracecovid19.util.BLEUtil
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

        private val writeDataPayload: MutableMap<String, ByteArray> = HashMap()
        private val readPayloadMap: MutableMap<String, ByteArray> = HashMap()
        private val deviceCharacteristicMap: MutableMap<String, UUID> = HashMap()

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
                            val value = BLEReadPayload(myTempId.tempId).getPayload()
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

        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice?,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?
        ) {
            if (device == null) {
                Log.e(TAG, "Write stopped - no device")
            }
            device?.let {
                DebugLogger.peripheral(
                    TAG,
                    "+++ onCharacteristicWriteRequest  - ${device.address} - preparedWrite: $preparedWrite , responseNeeded: $responseNeeded"
                )
                Log.i(
                    TAG,
                    "onCharacteristicWriteRequest from ${device.address} - $requestId - $offset"
                )

                if (BLE.supportsCharUUID(characteristic.uuid)) {
                    deviceCharacteristicMap[device.address] = characteristic.uuid
                    var valuePassed = ""
                    value?.let {
                        valuePassed = String(value, Charsets.UTF_8)
                    }
                    Log.i(TAG, "onCharacteristicWriteRequest from ${device.address} - $valuePassed")
                    if (value != null) {
                        var dataBuffer = writeDataPayload[device.address]

                        if (dataBuffer == null) {
                            dataBuffer = ByteArray(0)
                        }

                        dataBuffer = dataBuffer.plus(value)
                        writeDataPayload[device.address] = dataBuffer

                        Log.i(TAG, "Accumulated characteristic: ${String(dataBuffer, Charsets.UTF_8)}")

                        if (preparedWrite && responseNeeded) {
                            Log.i(TAG, "Sending response offset: ${dataBuffer.size}")
                            bluetoothGattServer?.sendResponse(
                                device,
                                requestId,
                                BluetoothGatt.GATT_SUCCESS,
                                dataBuffer.size,
                                value
                            )
                        }

                        //ios has this flag to false
                        if (!preparedWrite) {
                            Log.i(TAG, "onCharacteristicWriteRequest - ${device.address} - preparedWrite: $preparedWrite")

                            saveDataReceived(device)

                            if (responseNeeded) {
                                bluetoothGattServer?.sendResponse(
                                    device,
                                    requestId,
                                    BluetoothGatt.GATT_SUCCESS,
                                    0,
                                    null
                                )
                            }
                        }
                    }
                } else {
                    Log.i(TAG, "unsupported characteristic UUID from ${device.address}")

                    if (responseNeeded) {
                        bluetoothGattServer?.sendResponse(
                            device,
                            requestId,
                            BluetoothGatt.GATT_FAILURE,
                            0,
                            null
                        )
                    }
                }
            }
        }

        override fun onExecuteWrite(device: BluetoothDevice, requestId: Int, execute: Boolean) {
            DebugLogger.peripheral(TAG, "+++ onExecuteWrite")
            super.onExecuteWrite(device, requestId, execute)
            val data = writeDataPayload[device.address]

            data.let { dataBuffer ->

                if (dataBuffer != null) {
                    Log.i(TAG, "onExecuteWrite - $requestId- ${device.address} - ${String(dataBuffer, Charsets.UTF_8)}")
                    saveDataReceived(device)
                    bluetoothGattServer?.sendResponse(
                        device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        null
                    )
                } else {
                    bluetoothGattServer?.sendResponse(
                        device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        0,
                        null
                    )
                }
            }
        }

        private fun saveDataReceived(device: BluetoothDevice) {
            DebugLogger.peripheral(TAG, "+++ saveDataReceived")
            var data = writeDataPayload[device.address]
            var charUUID = deviceCharacteristicMap[device.address]

            charUUID?.let {
                data?.let {data ->
                    try {
                        device.let {
                            // TODO: ここでセントラルのwriteの情報で接続情報を作成するが、onExecuteWriteはAndroidでも呼ばれる？
                            // TODO: iOSでonCharacteristicWriteRequestが呼ばれたときのみ？
                            /*
                            val connectionRecord =
                                bluetraceImplementation.peripheral.processWriteRequestDataReceived(
                                    data,
                                    device.address
                                )
                            //connectionRecord will not be null if the deserializing was a success, save it
                            connectionRecord?.let {
                                // TODO: ここで接続情報を通知する理由は？
                                BLEUtil.broadcastStreetPassReceived(context, connectionRecord)
                            }
                             */
                            val payload = BLEWritePayload.fromPayload(data)
                            val connectionRecord = ConnectionRecord(
                                BLEType.PERIPHERAL,
                                payload.i,
                                payload.rs,
                                payload.txPower
                            )
                            BLEUtil.broadcastStreetPassReceived(context, connectionRecord)
                            DebugLogger.peripheral(TAG, "write request data = ${String(data, Charsets.UTF_8)}")
                        }
                    } catch (e: Throwable) {
                        Log.e(TAG, "Failed to process write payload - ${e.message}")
                    }

                    // TODO: これもここで通知する理由は？ここでペリフェラルのwrite？で通知するとブラックリストに入るので、セントラルのスキャンで引っかからなくなるのでは？
                    BLEUtil.broadcastDeviceProcessed(context, device.address)
                    // TODO: ここに来ないとクリアされないけど、大丈夫なの？
                    writeDataPayload.remove(device.address)
                    readPayloadMap.remove(device.address)
                    deviceCharacteristicMap.remove(device.address)
                }
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
