package jp.co.tracecovid19.screen.common

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import jp.co.tracecovid19.BuildConfig
import jp.co.tracecovid19.R
import jp.co.tracecovid19.extension.setUpToolBar
import jp.co.tracecovid19.ui.ProgressHUD
import kotlinx.android.synthetic.main.activity_web.*



class WebActivity: AppCompatActivity() {

    companion object {
        const val KEY = "jp.co.tracecovid19.screen.common.WebActivity"
    }

    private lateinit var transitionEntity: WebTransitionEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初期設定
        initialize()
        // viewの初期設定
        setupViews()
    }

    override fun onStart() {
        super.onStart()
        webView.loadUrl(transitionEntity.urlString)
    }

    override fun onDestroy() {
        webView.webViewClient = null
        super.onDestroy()
    }

    private fun initialize() {
        setContentView(R.layout.activity_web)

        // 引き継ぎデータの取り出し
        intent?.let { intent ->
            (intent.getSerializableExtra(KEY) as? WebTransitionEntity)?.let { entity ->
                // 引き継ぎデータあり
                transitionEntity = entity
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupViews() {
        // 初期は非表示
        webView.visibility = View.GONE
        // ツールバー
        setUpToolBar(toolBar, transitionEntity.title, transitionEntity.subTitle)

        // WebView
        webView.settings.javaScriptEnabled = true
        if (transitionEntity.zoomEnabled) {
            webView.settings.useWideViewPort = true
            webView.settings.loadWithOverviewMode = true
            webView.settings.builtInZoomControls = true
        }
        webView.webViewClient = object: WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                ProgressHUD.show(this@WebActivity)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                ProgressHUD.hide()
                webView.visibility = View.VISIBLE
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                if (BuildConfig.IS_IGONORE_SSL_ERROR) {
                    handler?.proceed()
                } else {
                    super.onReceivedSslError(view, handler, error)
                }
            }

            override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            }

        }
    }
}