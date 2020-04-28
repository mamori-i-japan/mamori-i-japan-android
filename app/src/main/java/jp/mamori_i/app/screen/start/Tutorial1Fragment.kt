package jp.mamori_i.app.screen.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.mamori_i.app.R
import kotlinx.android.synthetic.main.fragment_tutorial_1.*

class Tutorial1Fragment(private val navigator: TutorialNavigator): Fragment() {

    companion object {
        const val KEY = "jp.mamori_i.app.screen.start.Tutorial1Fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初期設定
        initialize()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tutorial_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // viewの初期設定
        setupViews()
    }

    private fun initialize() {
    }

    private fun setupViews() {
        nextButton.setOnClickListener {
            navigator.goToNext(TutorialNavigator.TutorialPageType.Tutorial1)
        }
    }
}
