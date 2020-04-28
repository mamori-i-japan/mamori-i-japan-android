package jp.mamori_i.app.screen.permission

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jp.mamori_i.app.R
import jp.mamori_i.app.extension.setUpToolBar
import jp.mamori_i.app.screen.home.HomeActivity
import kotlinx.android.synthetic.main.activity_permission_setting.*

class PermissionSettingActivity: AppCompatActivity(), PermissionSettingNavigator {
    companion object {
        const val KEY = "jp.mamori_i.app.screen.permission.PermissionSettingActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初期設定
        initialize()
        // viewの初期設定
        setupViews()
        // viewModelとのbind
        bind()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            // バックキー押下時はアプリをバックグラウンドに落とす
            moveTaskToBack(true)
        }
    }

    private fun initialize() {
        setContentView(R.layout.activity_permission_setting)
    }

    private fun setupViews() {
        setUpToolBar(toolBar, "利用いただくために", "", false)
        supportFragmentManager
            .beginTransaction()
            .replace(containerView.id, BLEPermissionSettingFragment(this))
            .addToBackStack(null)
            .commit()
    }

    private fun bind() {
    }

    override fun goToNext(pageType: PermissionSettingNavigator.PermissionSettingPageType) {
        when(pageType) {
            PermissionSettingNavigator.PermissionSettingPageType.BLE -> {
                val intent = Intent(this, HomeActivity::class.java)
                this.startActivity(intent)
            }
        }
    }
}