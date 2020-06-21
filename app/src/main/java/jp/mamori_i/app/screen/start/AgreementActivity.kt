package jp.mamori_i.app.screen.start

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import jp.mamori_i.app.R
import jp.mamori_i.app.extension.setUpToolBar
import jp.mamori_i.app.screen.profile.InputPrefectureActivity
import kotlinx.android.synthetic.main.activity_agreement.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AgreementActivity: AppCompatActivity(), AgreementNavigator {

    companion object {
        const val KEY = "jp.mamori_i.app.screen.start.AgreementActivity"
    }

    private val viewModel: AgreementViewModel by viewModel()
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

    private fun initialize() {
        setContentView(R.layout.activity_agreement)
        viewModel.navigator = this
    }

    private fun setupViews() {
        setUpToolBar(toolBar, getString(R.string.toolbar_title_agreement))

        agreeButton.setOnClickListener {
            viewModel.onClickAgree()
        }

        linkButton.setOnClickListener {
            viewModel.onClickAgreementLink()
        }
    }

    private fun bind() {
    }

    override fun goToInputPrefecture() {
        val intent = Intent(this, InputPrefectureActivity::class.java)
        this.startActivity(intent)
    }

    override fun openWebBrowser(uri: Uri) {
        Toast.makeText(this, "URLを設定して必要な情報に誘導します", Toast.LENGTH_SHORT).show()
        //val intent = Intent(Intent.ACTION_VIEW, uri)
        //this.startActivity(intent)
    }
}