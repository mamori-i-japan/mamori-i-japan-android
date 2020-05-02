package jp.mamori_i.app.screen.menu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import jp.mamori_i.app.R
import jp.mamori_i.app.extension.setUpToolBar
import jp.mamori_i.app.extension.showErrorDialog
import jp.mamori_i.app.screen.start.SplashActivity
import jp.mamori_i.app.screen.trace.TraceDataUploadActivity
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.activity_menu.toolBar
import kotlinx.android.synthetic.main.activity_setting.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MenuActivity: AppCompatActivity(), MenuNavigator {
    companion object {
        const val KEY = "jp.mamori_i.app.screen.menu.MenuActivity"
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

    override fun onResume() {
        super.onResume()
        viewModel.fetchProfile(this)
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
    }

    private fun bind() {
        viewModel.menuItems
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { menuItems ->
                containerView.removeAllViews()
                menuItems.forEach {
                    containerView.addView(MenuListItemView(this, it))
                }
            }.addTo(disposable)

        viewModel.fetchError
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { error ->
                showErrorDialog(error)
            }.addTo(disposable)
    }

    override fun goToSetting() {
        val intent = Intent(this, SettingActivity::class.java)
        this.startActivity(intent)
    }

    override fun goToTraceDataUpload() {
        val intent = Intent(this, TraceDataUploadActivity::class.java)
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

    // TODO デバッグ用
    override fun goToSplash() {
        val intent = Intent(this, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        this.startActivity(intent)
    }
}