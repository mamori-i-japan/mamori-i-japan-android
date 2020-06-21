package jp.mamori_i.app.screen.menu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import jp.mamori_i.app.R
import jp.mamori_i.app.extension.handleError
import jp.mamori_i.app.extension.setUpToolBar
import jp.mamori_i.app.screen.profile.InputPrefectureActivity
import jp.mamori_i.app.screen.profile.InputPrefectureTransitionEntity
import jp.mamori_i.app.ui.ProgressHUD
import kotlinx.android.synthetic.main.activity_menu.toolBar
import kotlinx.android.synthetic.main.activity_setting.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingActivity: AppCompatActivity(), SettingNavigator {
    companion object {
        const val KEY = "jp.mamori_i.app.screen.menu.SettingActivity"
    }

    private val viewModel: SettingViewModel by viewModel()
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
        // 情報の取得
        viewModel.fetchProfile(this)
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

    private fun initialize() {
        setContentView(R.layout.activity_setting)
        viewModel.navigator = this
    }

    private fun setupViews() {
        // ツールバー
        setUpToolBar(toolBar, getString(R.string.toolbar_title_setting))

        prefectureSelectButton.setOnClickListener {
            viewModel.onClickPrefecture()
        }
    }

    private fun bind() {
        viewModel.profile
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { profile ->
                prefectureValueTextView.text = profile.prefectureType().description()
            }.addTo(disposable)

        viewModel.error
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { error ->
                handleError(error)
            }.addTo(disposable)
    }

    override fun showProgress() {
        ProgressHUD.show(this)
    }

    override fun hideProgress() {
        ProgressHUD.hide()
    }

    override fun goToInputPrefecture(transitionEntity: InputPrefectureTransitionEntity) {
        val intent = Intent(this, InputPrefectureActivity::class.java)
        intent.putExtra(InputPrefectureActivity.KEY, transitionEntity)
        this.startActivity(intent)
    }
}