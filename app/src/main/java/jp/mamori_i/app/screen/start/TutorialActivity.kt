package jp.mamori_i.app.screen.start

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import jp.mamori_i.app.R
import jp.mamori_i.app.extension.setUpToolBar
import kotlinx.android.synthetic.main.activity_tutorial.*


class TutorialActivity: AppCompatActivity(),
    TutorialNavigator {
    companion object {
        const val KEY = "jp.mamori_i.app.screen.start.TutorialActivity"
    }

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
        if (supportFragmentManager.backStackEntryCount > 1) {
            if (supportFragmentManager.backStackEntryCount == 2) {
                // 先頭に戻るときはToolBarを非表示にする
                toolBar.visibility = View.INVISIBLE
            }
            supportFragmentManager.popBackStack()
        } else {
            // バックキー押下時はアプリをバックグラウンドに落とす
            moveTaskToBack(true)
        }
    }

    private fun initialize() {
        setContentView(R.layout.activity_tutorial)
    }

    private fun setupViews() {
        setUpToolBar(toolBar, "") {
            this.onBackPressed()
        }

        toolBar.visibility = View.INVISIBLE // 先頭の時は非表示
        supportFragmentManager
            .beginTransaction()
            .replace(containerView.id, Tutorial1Fragment(this))
            .addToBackStack(null)
            .commit()
    }

    private fun bind() {
    }

    override fun goToNext(pageType: TutorialNavigator.TutorialPageType) {
        when(pageType) {
            TutorialNavigator.TutorialPageType.Tutorial1 -> {
                toolBar.visibility = View.VISIBLE
                supportFragmentManager
                    .beginTransaction()
                    .replace(containerView.id, Tutorial2Fragment(this))
                    .addToBackStack(null)
                    .commit()
            }
            TutorialNavigator.TutorialPageType.Tutorial2 -> {
                toolBar.visibility = View.VISIBLE
                supportFragmentManager
                    .beginTransaction()
                    .replace(containerView.id, Tutorial3Fragment(this))
                    .addToBackStack(null)
                    .commit()

            }
            TutorialNavigator.TutorialPageType.Tutorial3 -> {
                val intent = Intent(this, AgreementActivity::class.java)
                this.startActivity(intent)
            }
        }
    }
}