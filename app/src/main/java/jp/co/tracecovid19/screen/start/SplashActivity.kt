package jp.co.tracecovid19.screen.start

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.co.tracecovid19.R
import jp.co.tracecovid19.extension.showErrorAlert
import jp.co.tracecovid19.screen.home.HomeActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class SplashActivity: AppCompatActivity(),
    SplashNavigator {
    companion object {
        const val KEY = "jp.co.tracecovid19.screen.start.SplashActivity"
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
        viewModel.launchError
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                showErrorAlert(it) {
                    viewModel.launch(this)
                }
            }
            .addTo(disposable)
    }

    override fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        this.startActivity(intent)
    }

    override fun goToTutorial() {
        val intent = Intent(this, TutorialActivity::class.java)
        this.startActivity(intent)
    }
}