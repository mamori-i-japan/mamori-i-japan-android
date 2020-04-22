package jp.co.tracecovid19.screen.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.reactivex.subjects.PublishSubject
import jp.co.tracecovid19.R
import kotlinx.android.synthetic.main.fragment_agreement_2.*

class Agreement2Fragment(private val navigator: AgreementNavigator): Fragment() {

    companion object {
        const val KEY = "jp.co.tracecovid19.screen.start.Agreement2Fragment"
    }

    val title = PublishSubject.create<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初期設定
        initialize()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_agreement_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // viewの初期設定
        setupViews()
    }

    private fun initialize() {
    }

    private fun setupViews() {
        // タイトル
        title.onNext("情報共有への同意")

        agreeButton.setOnClickListener {
            navigator.goToNext(AgreementNavigator.AgreementPageType.Agreement2)
        }
    }
}
