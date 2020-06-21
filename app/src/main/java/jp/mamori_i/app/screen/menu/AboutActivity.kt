package jp.mamori_i.app.screen.menu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jp.mamori_i.app.BuildConfig
import jp.mamori_i.app.R
import jp.mamori_i.app.extension.setUpToolBar
import kotlinx.android.synthetic.main.activity_about.*


class AboutActivity: AppCompatActivity() {
    companion object {
        const val KEY = "jp.mamori_i.app.screen.menu.AboutActivity"
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
        setUpToolBar(toolBar, getString(R.string.toolbar_title_about))
        versionTextView.text = BuildConfig.VERSION_NAME
    }
}