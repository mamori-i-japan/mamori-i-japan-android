package jp.co.tracecovid19.screen.permission

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import jp.co.tracecovid19.R
import jp.co.tracecovid19.util.BLEUtil
import kotlinx.android.synthetic.main.fragment_tutorial_1.nextButton
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class BLEPermissionSettingFragment(private val navigator: PermissionSettingNavigator): Fragment() {

    companion object {
        const val KEY = "jp.co.tracecovid19.screen.setting.BLEPermissionSettingFragment"
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 456
        private const val REQUEST_ENABLE_BT = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初期設定
        initialize()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ble_permission_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // viewの初期設定
        setupViews()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_ACCESS_LOCATION -> {
                goNext()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT) {
            // BLEを許可しようがしまいが、先に進む
            setupPermissionsAndSettings()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun initialize() {
    }

    private fun setupViews() {
        nextButton.setOnClickListener {
            if (enableBluetooth()) {
                setupPermissionsAndSettings()
            }
        }
    }

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager =
            activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private fun enableBluetooth(): Boolean {
        bluetoothAdapter?.let {
            if (!it.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                return false
            }
            return true
        } ?: run {
            // TODO: ここどうする？そもそもBLEが使えない機種の可能性がある
            return false
        }
    }

    @AfterPermissionGranted(PERMISSION_REQUEST_ACCESS_LOCATION)
    private fun setupPermissionsAndSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context?.let {
                val perms = BLEUtil.getRequiredPermissions()
                if (EasyPermissions.hasPermissions(it, *perms)) {
                    goNext()
                } else {
                    EasyPermissions.requestPermissions(
                        this, getString(R.string.permission_location_rationale),
                        PERMISSION_REQUEST_ACCESS_LOCATION, *perms
                    )
                }
            }
        }
    }

    private fun goNext() {
        navigator.goToNext(PermissionSettingNavigator.PermissionSettingPageType.BLE)
    }
}

