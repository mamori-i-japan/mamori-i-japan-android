package jp.mamori_i.app.screen.start

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import jp.mamori_i.app.R
import jp.mamori_i.app.extension.showSimpleMessageAlert
import jp.mamori_i.app.screen.home.HomeActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class SplashActivity: AppCompatActivity(),
    SplashNavigator {
    companion object {
        const val KEY = "jp.mamori_i.app.screen.start.SplashActivity"
    }

    private val viewModel: SplashViewModel by viewModel()
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

    override fun onStart() {
        super.onStart()
        // 起動開始
        viewModel.launch(this)
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

    private fun initialize() {
        setContentView(R.layout.activity_splash)
        viewModel.navigator = this
    }

    private fun setupViews() {
    }

    private fun bind() {
    }

    override fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        this.startActivity(intent)
    }

    override fun goToTutorial() {
        val intent = Intent(this, TutorialActivity::class.java)
        this.startActivity(intent)
    }

    override fun showForceUpdateDialog(message: String, uri: Uri) {
        showSimpleMessageAlert(message) {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            this.startActivity(intent)
            finish()
        }
    }

    override fun showMaintenanceDialog(message: String) {
        showSimpleMessageAlert(message) {
            finish()
        }
    }
}