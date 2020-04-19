package jp.co.tracecovid19.screen.menu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jp.co.tracecovid19.R
import jp.co.tracecovid19.extension.setUpToolBar
import kotlinx.android.synthetic.main.activity_license.*


class LicenseActivity: AppCompatActivity() {
    companion object {
        const val KEY = "jp.co.tracecovid19.screen.menu.LicenseActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初期設定
        initialize()
        // viewの初期設定
        setupViews()
    }

    private fun initialize() {
        setContentView(R.layout.activity_license)
    }

    private fun setupViews() {
        // ツールバー
        setUpToolBar(toolBar, "ライセンス")
    }
}