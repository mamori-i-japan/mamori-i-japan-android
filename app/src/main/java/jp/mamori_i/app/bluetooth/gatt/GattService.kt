package jp.mamori_i.app.bluetooth.gatt

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import jp.mamori_i.app.BuildConfig
import java.util.*

class GattService(context: Context, serviceUUIDString: String) {

    private var serviceUUID = UUID.fromString(serviceUUIDString)

    var gattService = BluetoothGattService(serviceUUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)

    private var characteristic: BluetoothGattCharacteristic = BluetoothGattCharacteristic(
        UUID.fromString(BuildConfig.CHARACTERISTIC_ID),
        BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
        BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE
    )

    init {
        gattService.addCharacteristic(characteristic)
    }
}