package jp.co.tracecovid19.screen.menu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import jp.co.tracecovid19.R
import jp.co.tracecovid19.extension.setUpToolBar
import jp.co.tracecovid19.screen.start.SplashActivity
import kotlinx.android.synthetic.main.activity_menu.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MenuActivity: AppCompatActivity(), MenuNavigator {
    companion object {
        const val KEY = "jp.co.tracecovid19.screen.menu.MenuActivity"
    }

    private val viewModel: MenuViewModel by viewModel()
    private val disposable: CompositeDisposable by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初期設定
        initialize()
        // viewの初期設定
        setupViews()
        // viewModelとのbind
        bind()
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

    private fun initialize() {
        setContentView(R.layout.activity_menu)
        viewModel.navigator = this
    }

    private fun setupViews() {
        // ツールバー
        setUpToolBar(toolBar, "メニュー")

        settingButton.setOnClickListener {
            viewModel.onClickSetting()
        }

        aboutButton.setOnClickListener {
            viewModel.onClickAbout()
        }

        licenseButton.setOnClickListener {
            viewModel.onClickLicense()
        }

        logoutButton.setOnClickListener {
            viewModel.onClickLogout()
        }

        restartButton.setOnClickListener {
            viewModel.onClickRestart()
        }
    }

    private fun bind() {
    }

    override fun goToSetting() {
        val intent = Intent(this, SettingActivity::class.java)
        this.startActivity(intent)
    }

    override fun goToAbout() {
        val intent = Intent(this, AboutActivity::class.java)
        this.startActivity(intent)
    }

    override fun goToLicense() {
        val intent = Intent(this, LicenseActivity::class.java)
        this.startActivity(intent)
    }

    override fun goToSplash() {
        val intent = Intent(this, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        this.startActivity(intent)
    }
}