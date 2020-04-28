package jp.mamori_i.app.screen.profile

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import jp.mamori_i.app.R
import jp.mamori_i.app.extension.setUpToolBar
import jp.mamori_i.app.extension.showErrorDialog
import jp.mamori_i.app.screen.register.*
import jp.mamori_i.app.ui.ProgressHUD
import kotlinx.android.synthetic.main.activity_input_job.*
import kotlinx.android.synthetic.main.activity_input_job.toolBar
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class InputJobActivity: AppCompatActivity(),
    InputJobNavigator {
    companion object {
        const val KEY = "jp.mamori_i.app.screen.profile.InputWorkActivity"
    }

    private val viewModel: InputJobViewModel by viewModel()
    private val disposable: CompositeDisposable by inject()
    private lateinit var transitionEntity: InputJobTransitionEntity

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
        setContentView(R.layout.activity_input_job)
        viewModel.navigator = this
        intent?.let { intent ->
            (intent.getSerializableExtra(KEY) as? InputJobTransitionEntity)?.let { entity ->
                // 引き継ぎデータあり
                transitionEntity = entity
            }
        }
    }

    private fun setupViews() {
        // ツールバー
        setUpToolBar(toolBar, "職業を選択")

        workInputText.requestFocus()
        executeButton.setOnClickListener {
            viewModel.onClickExecuteButton(workInputText.text.toString(),
                transitionEntity.profile,
                transitionEntity.isRegistrationFlow,
                this)
        }

        if (transitionEntity.isRegistrationFlow) {
            executeButton.text = "次へ"
        } else {
            executeButton.text = "更新する"
            workInputText.setText(transitionEntity.profile.job, TextView.BufferType.NORMAL)
        }
    }

    private fun bind() {
        viewModel.updateError
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

    override fun goToInputPhoneNumber(transitionEntity: InputPhoneNumberTransitionEntity) {
        val intent = Intent(this, InputPhoneNumberActivity::class.java)
        intent.putExtra(InputPhoneNumberActivity.KEY, transitionEntity)
        this.startActivity(intent)
    }

    override fun finishWithCompleteMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        finish()
    }
}