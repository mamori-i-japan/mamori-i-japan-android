package jp.co.tracecovid19.streetpass

import android.content.Context
import android.util.Log
import jp.co.tracecovid19.bluetooth.gatt.GattServer
import jp.co.tracecovid19.bluetooth.gatt.GattService
import jp.co.tracecovid19.idmanager.TempIdManager
import jp.co.tracecovid19.logger.DebugLogger

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