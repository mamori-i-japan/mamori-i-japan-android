package jp.co.tracecovid19.screen.menu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jp.co.tracecovid19.BuildConfig
import jp.co.tracecovid19.R
import jp.co.tracecovid19.extension.setUpToolBar
import kotlinx.android.synthetic.main.activity_about.*


class AboutActivity: AppCompatActivity() {
    companion object {
        const val KEY = "jp.co.tracecovid19.screen.menu.AboutActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初期設定
        initialize()
        // viewの初期設定
        setupViews()
    }

    private fun initialize() {
        setContentView(R.layout.activity_about)
    }

    private fun setupViews() {
        // ツールバー
        setUpToolBar(toolBar, "このアプリについて")
        versionTextView.text = BuildConfig.VERSION_NAME
    }
}