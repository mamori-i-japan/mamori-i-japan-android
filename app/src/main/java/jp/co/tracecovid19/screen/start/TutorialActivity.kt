package jp.co.tracecovid19.screen.start

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jp.co.tracecovid19.R
import kotlinx.android.synthetic.main.activity_tutorial.*


class TutorialActivity: AppCompatActivity(),
    TutorialNavigator {
    companion object {
        const val KEY = "jp.co.tracecovid19.screen.start.TutorialActivity"
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
                supportFragmentManager
                    .beginTransaction()
                    .replace(containerView.id, Tutorial2Fragment(this))
                    .addToBackStack(null)
                    .commit()
            }
            TutorialNavigator.TutorialPageType.Tutorial2 -> {
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