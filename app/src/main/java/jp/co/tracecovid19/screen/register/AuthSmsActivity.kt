package jp.co.tracecovid19.screen.register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.co.tracecovid19.R
import jp.co.tracecovid19.extension.*
import jp.co.tracecovid19.screen.common.TraceCovid19Error.Action.*
import jp.co.tracecovid19.screen.permission.PermissionSettingActivity
import jp.co.tracecovid19.ui.CodeInputText
import jp.co.tracecovid19.ui.ProgressHUD
import kotlinx.android.synthetic.main.activity_auth_sms.*
import kotlinx.android.synthetic.main.ui_code_input_text.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class AuthSmsActivity: AppCompatActivity(), AuthSmsNavigator {
    companion object {
        const val KEY = "jp.co.tracecovid19.screen.start.TutorialActivity"
    }

    private val viewModel: AuthSmsViewModel by viewModel()
    private val disposable: CompositeDisposable by inject()
    private lateinit var transitionEntity: AuthSmsTransitionEntity

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
        setContentView(R.layout.activity_auth_sms)
        viewModel.navigator = this
        // 引き継ぎデータの取り出し
        intent?.let { intent ->
            (intent.getSerializableExtra(KEY) as? AuthSmsTransitionEntity)?.let { entity ->
                // 引き継ぎデータあり
                transitionEntity = entity
            }
        }
    }

    private fun setupViews() {
        // ツールバー
        setUpToolBar(toolBar, "確認番号入力")

        codeInputText.setListener(object: CodeInputText.CodeInputTextListener {
            override fun inputFinished(code: String) {
                hideKeyboard()
                viewModel.executeAuth(
                    code,
                    transitionEntity.verificationId,
                    transitionEntity.profile,
                    this@AuthSmsActivity)
            }
        })
        codeInputText.editText.requestFocus()
        changePhoneNumberButton.setOnClickListener {
            finish()
        }
        codeInputText.setOnClickListener {
            showKeyboard(codeInputText.editText)
        }
    }

    private fun bind() {
        viewModel.authError
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { error ->
                when(error.action) {
                    DialogCloseOnly -> {
                        codeInputText.clear()
                        showErrorAlert(error)
                    }
                    Inline -> {
                        codeInputText.showError(error.message)
                    }
                    DialogBack -> {
                        showErrorAlert(error) {
                            finish()
                        }
                    }
                    else -> { showErrorAlert(error) }
                }
            }
            .addTo(disposable)

        viewModel.loginError
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { error ->
                when(error.action) {
                    DialogRetry -> {
                        showErrorAlert(error) {
                            viewModel.executeLogin(transitionEntity.profile)
                        }
                    }
                    DialogBack -> {
                        showErrorAlert(error) {
                            finish()
                        }
                    }
                    else -> { showErrorAlert(error) }
                }
            }
            .addTo(disposable)
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
}