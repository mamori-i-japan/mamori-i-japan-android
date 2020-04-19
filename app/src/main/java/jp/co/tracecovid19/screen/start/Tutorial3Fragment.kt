package jp.co.tracecovid19.screen.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.co.tracecovid19.R
import kotlinx.android.synthetic.main.fragment_tutorial_3.*

class Tutorial3Fragment(private val navigator: TutorialNavigator): Fragment() {

    companion object {
        const val KEY = "jp.co.tracecovid19.screen.start.Tutorial3Fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初期設定
        initialize()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tutorial_3, container, false)
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
            navigator.goToNext(TutorialNavigator.TutorialPageType.Tutorial3)
        }
    }
}
