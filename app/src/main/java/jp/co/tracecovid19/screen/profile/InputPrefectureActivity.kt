package jp.co.tracecovid19.screen.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import jp.co.tracecovid19.R
import jp.co.tracecovid19.data.model.PrefectureType
import jp.co.tracecovid19.extension.setUpToolBar
import jp.co.tracecovid19.extension.showErrorAlert
import jp.co.tracecovid19.ui.ProgressHUD
import kotlinx.android.synthetic.main.activity_input_prefecture.*
import kotlinx.android.synthetic.main.activity_input_prefecture.toolBar
import kotlinx.android.synthetic.main.ui_select_text.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class InputPrefectureActivity: AppCompatActivity(),
    InputPrefectureNavigator {
    companion object {
        const val KEY = "jp.co.tracecovid19.screen.profile.InputPrefectureActivity"
    }

    private val viewModel: InputPrefectureViewModel by viewModel()
    private val disposable: CompositeDisposable by inject()
    private lateinit var transitionEntity: InputPrefectureTransitionEntity

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
        setContentView(R.layout.activity_input_prefecture)
        viewModel.navigator = this
        intent?.let { intent ->
            (intent.getSerializableExtra(KEY) as? InputPrefectureTransitionEntity)?.let { entity ->
                // 引き継ぎデータあり
                transitionEntity = entity
            }
        }
    }

    private fun setupViews() {
        // ツールバー
        setUpToolBar(toolBar, "都道府県入力")
        executeButton.setOnClickListener {
            viewModel.onClickExecuteButton(prefecturesSelectText.selectItem() as? PrefectureType,
                transitionEntity.profile,
                transitionEntity.isRegistrationFlow,
                this)
        }
        prefecturesSelectText.setSelectDataSource(
            PrefectureType.selectableValues(),
            transitionEntity.profile.prefectureType()
        )
        if (transitionEntity.isRegistrationFlow) {
            executeButton.text = "次へ"
        } else {
            executeButton.text = "更新する"
            prefecturesSelectText.setItem(transitionEntity.profile.prefectureType())
        }
    }

    private fun bind() {
        prefecturesSelectText.editText.textChanges()
            .map { prefecture ->
                prefecture.isNotBlank()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { enabled ->
                executeButton.isEnabled = enabled
            }.addTo(disposable)

        viewModel.updateError
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { error ->
                showErrorAlert(error)
            }.addTo(disposable)
    }

    override fun showProgress() {
        ProgressHUD.show(this)
    }

    override fun hideProgress() {
        ProgressHUD.hide()
    }

    override fun goToInputWork(transitionEntity: InputJobTransitionEntity) {
        val intent = Intent(this, InputJobActivity::class.java)
        intent.putExtra(InputJobActivity.KEY, transitionEntity)
        this.startActivity(intent)
    }

    override fun finishWithCompleteMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        finish()
    }
}