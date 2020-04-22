package jp.co.tracecovid19.bluetooth.gatt

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import jp.co.tracecovid19.BuildConfig
import java.util.*

class GattService(context: Context, serviceUUIDString: String) {

    private var serviceUUID = UUID.fromString(serviceUUIDString)

    var gattService = BluetoothGattService(serviceUUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)

    private var characteristicV2: BluetoothGattCharacteristic = BluetoothGattCharacteristic(
        UUID.fromString(BuildConfig.V2_CHARACTERISTIC_ID),
        BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_WRITE,
        BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE
    )

    init {
        gattService.addCharacteristic(characteristicV2)
    }
}