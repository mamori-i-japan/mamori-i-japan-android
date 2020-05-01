package jp.mamori_i.app.screen.start

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jp.mamori_i.app.R
import jp.mamori_i.app.extension.setUpToolBar
import jp.mamori_i.app.screen.profile.InputPrefectureActivity
import kotlinx.android.synthetic.main.activity_tutorial.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class TutorialActivity: AppCompatActivity(), TutorialNavigator {

    companion object {
        const val KEY = "jp.mamori_i.app.screen.start.TutorialActivity"
    }

    private val viewModel: TutorialViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初期設定
        initialize()
        // viewの初期設定
        setupViews()
        // viewModelとのbind
        bind()
    }

    override fun onBackPressed() {
        // バックキー押下時はアプリをバックグラウンドに落とす
        moveTaskToBack(true)
    }

    private fun initialize() {
        setContentView(R.layout.activity_tutorial)
        viewModel.navigator = this
    }

    private fun setupViews() {
        setUpToolBar(toolBar, "", "", false)

        startButton.setOnClickListener {
            viewModel.onClickStart()
        }

        linkButton.setOnClickListener {
            viewModel.onClickWebLink()
        }
    }

    private fun bind() {
    }

    override fun goToInputPrefecture() {
        val intent = Intent(this, InputPrefectureActivity::class.java)
        this.startActivity(intent)
    }

    override fun openWebBrowser(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        this.startActivity(intent)
    }
}