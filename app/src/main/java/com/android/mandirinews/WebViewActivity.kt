package com.android.mandirinews

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.android.mandirinews.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra("url")

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = MyWebViewClient()

        if (url != null) {
            binding.webView.loadUrl(url)
        }
    }

    inner class MyWebViewClient : WebViewClient() {
        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            // Setelah URL dimuat di WebView, tautan lain akan dibuka di WebView yang sama
            if (url != null) {
                view?.loadUrl(url)
            }
            return true
        }
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}