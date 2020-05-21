package jp.mamori_i.app.screen.permission

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jp.mamori_i.app.R
import jp.mamori_i.app.extension.setUpToolBar
import jp.mamori_i.app.screen.home.HomeActivity
import jp.mamori_i.app.util.BLEUtil
import kotlinx.android.synthetic.main.activity_permission_setting.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class PermissionSettingActivity: AppCompatActivity() {
    companion object {
        const val KEY = "jp.mamori_i.app.screen.permission.PermissionSettingActivity"
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 456
        private const val REQUEST_ENABLE_BT = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初期設定
        initialize()
        // viewの初期設定
        setupViews()
    }

    override fun onBackPressed() {
        // バックキー押下時はアプリをバックグラウンドに落とす
        moveTaskToBack(true)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_ACCESS_LOCATION -> {
                goToHome()
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
        setContentView(R.layout.activity_permission_setting)
    }

    private fun setupViews() {
        setUpToolBar(toolBar, "利用いただくために", "")
        settingButton.setOnClickListener {
            if (enableBluetooth()) {
                setupPermissionsAndSettings()
            }
        }
    }

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
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
            return false
        }
    }

    @AfterPermissionGranted(PERMISSION_REQUEST_ACCESS_LOCATION)
    private fun setupPermissionsAndSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val perms = BLEUtil.getRequiredPermissions()
            if (EasyPermissions.hasPermissions(this, *perms)) {
                goToHome()
            } else {
                EasyPermissions.requestPermissions(
                    this, getString(R.string.permission_location_rationale),
                    PERMISSION_REQUEST_ACCESS_LOCATION, *perms
                )
            }
        }
    }

    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        this.startActivity(intent)
    }
}