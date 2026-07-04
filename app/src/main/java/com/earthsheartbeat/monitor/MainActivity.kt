package com.earthsheartbeat.monitor

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var web: WebView
    private val reloadHandler = Handler(Looper.getMainLooper())
    private val oneHour = 60L * 60L * 1000L

    private val reloadTask = object : Runnable {
        override fun run() {
            web.reload()
            reloadHandler.postDelayed(this, oneHour)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        web = findViewById(R.id.webview)
        web.setBackgroundColor(Color.parseColor("#020407"))
        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
        }
        web.webViewClient = WebViewClient()
        web.loadUrl(Feeds.PAGE)
    }

    override fun onResume() {
        super.onResume()
        // app-level hourly refresh (the page also self-refreshes)
        reloadHandler.postDelayed(reloadTask, oneHour)
    }

    override fun onPause() {
        super.onPause()
        reloadHandler.removeCallbacks(reloadTask)
    }

    override fun onBackPressed() {
        if (web.canGoBack()) web.goBack() else super.onBackPressed()
    }
}
