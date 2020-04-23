package jp.co.tracecovid19.screen.menu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import jp.co.tracecovid19.R
import jp.co.tracecovid19.extension.setUpToolBar
import jp.co.tracecovid19.extension.showErrorDialog
import jp.co.tracecovid19.screen.profile.InputPrefectureActivity
import jp.co.tracecovid19.screen.profile.InputPrefectureTransitionEntity
import jp.co.tracecovid19.screen.profile.InputJobActivity
import jp.co.tracecovid19.screen.profile.InputJobTransitionEntity
import kotlinx.android.synthetic.main.activity_menu.toolBar
import kotlinx.android.synthetic.main.activity_setting.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingActivity: AppCompatActivity(), SettingNavigator {
    companion object {
        const val KEY = "jp.co.tracecovid19.screen.menu.SettingActivity"
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
        setUpToolBar(toolBar, "設定")

        prefectureButton.setOnClickListener {
            viewModel.onClickPrefecture()
        }

        workButton.setOnClickListener {
            viewModel.onClickWork()
        }
    }

    private fun bind() {
        viewModel.profile
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { profile ->
                resultTextView.text = "都道府県: " +
                        profile.prefectureType().description() +
                        "\n" +
                        "職業: " +
                        profile.job
            }.addTo(disposable)

        viewModel.fetchError
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { error ->
                showErrorDialog(error)
            }.addTo(disposable)
    }

    override fun goToInputPrefecture(transitionEntity: InputPrefectureTransitionEntity) {
        val intent = Intent(this, InputPrefectureActivity::class.java)
        intent.putExtra(InputPrefectureActivity.KEY, transitionEntity)
        this.startActivity(intent)
    }

    override fun goToInputWork(transitionEntity: InputJobTransitionEntity) {
        val intent = Intent(this, InputJobActivity::class.java)
        intent.putExtra(InputJobActivity.KEY, transitionEntity)
        this.startActivity(intent)
    }
}