package jp.mamori_i.app.screen.start

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import jp.mamori_i.app.R
import jp.mamori_i.app.extension.setUpToolBar
import jp.mamori_i.app.extension.showErrorDialog
import jp.mamori_i.app.screen.permission.PermissionSettingActivity
import jp.mamori_i.app.ui.ProgressHUD
import kotlinx.android.synthetic.main.activity_agreement.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AgreementActivity: AppCompatActivity(), AgreementNavigator {

    companion object {
        const val KEY = "jp.mamori_i.app.screen.start.AgreementActivity"
    }

    private val viewModel: AgreementViewModel by viewModel()
    private val disposable: CompositeDisposable by inject()
    private lateinit var transitionEntity: AgreementTransitionEntity

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
        setContentView(R.layout.activity_agreement)
        viewModel.navigator = this
        intent?.let { intent ->
            (intent.getSerializableExtra(KEY) as? AgreementTransitionEntity)?.let { entity ->
                // 引き継ぎデータあり
                transitionEntity = entity
            }
        }
    }

    private fun setupViews() {
        setUpToolBar(toolBar, "利用規約への同意")

        agreeButton.setOnClickListener {
            viewModel.onClickAgree(transitionEntity.prefectureType)
        }

        linkButton.setOnClickListener {
            viewModel.onClickAgreementLink()
        }
    }

    private fun bind() {
        viewModel.loginError
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { error ->
                showErrorDialog(error)
            }.addTo(disposable)
    }

    override fun showProgress() {
        ProgressHUD.show(this)
    }

    override fun hideProgress() {
        ProgressHUD.hide()
    }

    override fun goToPermissionSetting() {
        val intent = Intent(this, PermissionSettingActivity::class.java)
        this.startActivity(intent)
    }

    override fun openWebBrowser(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        this.startActivity(intent)
    }
}