package jp.mamori_i.app.streetpass

import android.content.Context
import android.util.Log
import jp.mamori_i.app.bluetooth.gatt.GattServer
import jp.mamori_i.app.bluetooth.gatt.GattService
import jp.mamori_i.app.idmanager.TempIdManager
import jp.mamori_i.app.logger.DebugLogger

class StreetPassServer(context: Context, private val serviceUUIDString: String, private val tempIdManager: TempIdManager) {

    companion object {
        private const val TAG = "StreetPassServer"
    }

    private var gattServer: GattServer? = setupGattServer(context, serviceUUIDString)

    private fun setupGattServer(context: Context, serviceUUIDString: String): GattServer? {
        DebugLogger.log(TAG, "setupGattServer")
        val gattServer = GattServer(context, serviceUUIDString, tempIdManager)
        val started = gattServer.startServer()

        if (started) {
            val readService = GattService(context, serviceUUIDString)
            gattServer.addService(readService)
            return gattServer
        }
        return null
    }

    fun tearDown() {
        DebugLogger.log(TAG, "tearDown")
        gattServer?.stop()
    }

    fun checkServiceAvailable(): Boolean {
        return gattServer?.bluetoothGattServer?.services?.filter {
            it.uuid.toString() == serviceUUIDString
        }?.isNotEmpty() ?: false
    }
}