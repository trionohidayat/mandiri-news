package com.android.mandirinews

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.android.mandirinews.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewBinding
    private lateinit var progressWeb: ProgressBar

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra("url")

        progressWeb = binding.progressWeb

        progressWeb.visibility = View.VISIBLE

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = MyWebViewClient()

        if (url != null) {
            binding.webView.loadUrl(url)
        }
    }

    inner class MyWebViewClient : WebViewClient() {
        private var loadingFinished = true
        private var redirect = false

        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (!loadingFinished) {
                redirect = true
            }

            loadingFinished = false
            if (url != null) {
                view?.loadUrl(url)
            }
            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            loadingFinished = false
            progressWeb.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            if (!redirect) {
                loadingFinished = true
                progressWeb.visibility = View.GONE
            } else {
                redirect = false
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}