package jp.co.tracecovid19.screen.start

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.co.tracecovid19.R
import jp.co.tracecovid19.data.model.Profile
import jp.co.tracecovid19.extension.setUpToolBar
import jp.co.tracecovid19.screen.profile.InputPrefectureActivity
import jp.co.tracecovid19.screen.profile.InputPrefectureTransitionEntity
import kotlinx.android.synthetic.main.activity_agreement.*
import kotlinx.android.synthetic.main.activity_agreement.toolBar
import org.koin.android.ext.android.inject

class AgreementActivity: AppCompatActivity(), AgreementNavigator {

    companion object {
        const val KEY = "jp.co.tracecovid19.screen.start.AgreementActivity"
    }

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

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    private fun initialize() {
        setContentView(R.layout.activity_agreement)
    }

    private fun setupViews() {
        setUpToolBar(toolBar, "") {
            this.onBackPressed()
        }
        supportFragmentManager
            .beginTransaction()
            .replace(containerView.id, Agreement1Fragment(this).apply {
                title.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy {
                        supportActionBar?.title = it
                    }
                    .addTo(disposable)
            })
            .addToBackStack(null)
            .commit()
    }

    private fun bind() {
    }

    override fun goToNext(pageType: AgreementNavigator.AgreementPageType) {
        when(pageType) {
            AgreementNavigator.AgreementPageType.Agreement1 -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(containerView.id, Agreement2Fragment(this).apply {
                        title.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeBy {
                                supportActionBar?.title = it
                            }
                            .addTo(disposable)
                    })
                    .addToBackStack(null)
                    .commit()
            }
            AgreementNavigator.AgreementPageType.Agreement2 -> {
                val intent = Intent(this, InputPrefectureActivity::class.java)
                intent.putExtra(InputPrefectureActivity.KEY, InputPrefectureTransitionEntity(Profile(), true))
                this.startActivity(intent)
            }
        }
    }

    override fun openWebBrowser(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        this.startActivity(intent)
    }
}