package jp.mamori_i.app.screen.register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.mamori_i.app.R
import jp.mamori_i.app.extension.*
import jp.mamori_i.app.screen.permission.PermissionSettingActivity
import jp.mamori_i.app.ui.ProgressHUD
import kotlinx.android.synthetic.main.activity_input_phone_number.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class InputPhoneNumberActivity: AppCompatActivity(), InputPhoneNumberNavigator {
    companion object {
        const val KEY = "jp.mamori_i.app.screen.register.InputPhoneNumberActivity"
    }

    private val viewModel: InputPhoneNumberViewModel by viewModel()
    private val disposable: CompositeDisposable by inject()
    private lateinit var transitionEntity: InputPhoneNumberTransitionEntity

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
        setContentView(R.layout.activity_input_phone_number)
        viewModel.navigator = this
        intent?.let { intent ->
            (intent.getSerializableExtra(KEY) as? InputPhoneNumberTransitionEntity)?.let { entity ->
                // 引き継ぎデータあり
                transitionEntity = entity
            }
        }
    }

    private fun setupViews() {
        // ツールバー
        setUpToolBar(toolBar, "電話番号確認")

        phoneNumberInputText.requestFocus()

        sendButton.setOnClickListener {
            hideKeyboard()
            viewModel.onClickSendButton(
                phoneNumberInputText.text.toString(),
                transitionEntity.profile,
                this)
        }
    }

    private fun bind() {
        phoneNumberInputText.textChanges()
            .map { phoneNum ->
                phoneNum.length == 11
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { enabled ->
                sendButton.isEnabled = enabled
            }.addTo(disposable)

        viewModel.sendError
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { error ->
                showErrorDialog(error)
            }
            .addTo(disposable)

        viewModel.loginError
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { error ->
                showErrorDialog(error)
            }
            .addTo(disposable)
    }

    override fun showProgress() {
        ProgressHUD.show(this)
    }

    override fun hideProgress() {
        ProgressHUD.hide()
    }

    override fun goToSmsAuth(transitionEntity: AuthSmsTransitionEntity) {
        val intent = Intent(this, AuthSmsActivity::class.java)
        intent.putExtra(AuthSmsActivity.KEY, transitionEntity)
        this.startActivity(intent)
    }

    override fun goToPermissionSetting() {
        val intent = Intent(this, PermissionSettingActivity::class.java)
        this.startActivity(intent)
    }
}