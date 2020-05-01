package jp.mamori_i.app.screen.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import jp.mamori_i.app.R
import jp.mamori_i.app.data.model.PrefectureType
import jp.mamori_i.app.extension.setUpToolBar
import jp.mamori_i.app.extension.showErrorDialog
import jp.mamori_i.app.screen.start.AgreementActivity
import jp.mamori_i.app.screen.start.AgreementTransitionEntity
import jp.mamori_i.app.ui.ProgressHUD
import kotlinx.android.synthetic.main.activity_input_prefecture.*
import kotlinx.android.synthetic.main.activity_input_prefecture.toolBar
import kotlinx.android.synthetic.main.ui_select_text.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class InputPrefectureActivity: AppCompatActivity(),
    InputPrefectureNavigator {
    companion object {
        const val KEY = "jp.mamori_i.app.screen.profile.InputPrefectureActivity"
    }

    private val viewModel: InputPrefectureViewModel by viewModel()
    private val disposable: CompositeDisposable by inject()
    private var transitionEntity: InputPrefectureTransitionEntity? = null

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
        setUpToolBar(toolBar, "都道府県選択")

        executeButton.setOnClickListener {
            val input = prefecturesSelectText.selectItem() as PrefectureType
            transitionEntity?.let {
                viewModel.onClickUpdateButton(input, this)
            }?: viewModel.onClickNextButton(input)
        }

        prefecturesSelectText.setSelectDataSource(
            PrefectureType.selectableValues(),
            transitionEntity?.selected ?: PrefectureType.Tokyo
        )

        transitionEntity?.let {
            executeButton.text = "設定する"
            prefecturesSelectText.setItem(it.selected)
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
                showErrorDialog(error)
            }.addTo(disposable)
    }

    override fun showProgress() {
        ProgressHUD.show(this)
    }

    override fun hideProgress() {
        ProgressHUD.hide()
    }

    override fun goToAgreement(transitionEntity: AgreementTransitionEntity) {
        val intent = Intent(this, AgreementActivity::class.java)
        intent.putExtra(AgreementActivity.KEY, transitionEntity)
        this.startActivity(intent)
    }

    override fun finishWithCompleteMessage(message: String) {
        finish()
    }
}