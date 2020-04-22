package jp.co.tracecovid19.screen.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import jp.co.tracecovid19.R
import kotlinx.android.synthetic.main.fragment_agreement_1.*

class Agreement1Fragment(private val navigator: AgreementNavigator): Fragment() {

    companion object {
        const val KEY = "jp.co.tracecovid19.screen.start.Agreement1Fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初期設定
        initialize()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_agreement_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // viewの初期設定
        setupViews()
    }

    private fun initialize() {
    }

    private fun setupViews() {
        agreeButton.setOnClickListener {
            navigator.goToNext(AgreementNavigator.AgreementPageType.Agreement1)
        }
        linkButton.setOnClickListener {
            // TODO 適当
            navigator.openWebBrowser("https://www.yahoo.co.jp".toUri())
        }
    }
}
