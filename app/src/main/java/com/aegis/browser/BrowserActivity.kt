package com.aegis.browser

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aegis.browser.utils.UrlUtils

class BrowserActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var urlBar: EditText
    private lateinit var progressBar: ProgressBar
    private var currentUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)

        webView = WebView(this).apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = false
                displayZoomControls = false
                saveFormData = false
                savePassword = false
            }
            setLayerType(View.LAYER_TYPE_HARDWARE, null)

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    return false
                }

                override fun onPageStarted(view: WebView, url: String, favicon: android.graphics.Bitmap?) {
                    progressBar.visibility = View.VISIBLE
                    urlBar.setText(url)
                    currentUrl = url
                }

                override fun onPageFinished(view: WebView, url: String) {
                    progressBar.visibility = View.GONE
                    urlBar.setText(url)
                    currentUrl = url
                }

                override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                    val url = request.url.toString()
                    if (url.contains("doubleclick.net") || url.contains("googleadservices.com")) {
                        return WebResourceResponse("text/plain", "UTF-8", null)
                    }
                    return super.shouldInterceptRequest(view, request)
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    progressBar.progress = newProgress
                }

                override fun onReceivedTitle(view: WebView, title: String) {
                    supportActionBar?.title = title
                }
            }
        }

        urlBar = findViewById(R.id.url_bar)
        progressBar = findViewById(R.id.progress_bar)

        val container: android.widget.FrameLayout = findViewById(R.id.webview_container)
        container.addView(webView, android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT
        ))

        urlBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                loadUrl(urlBar.text.toString())
                true
            } else false
        }

        findViewById<TextView>(R.id.btn_back).setOnClickListener { goBack() }
        findViewById<TextView>(R.id.btn_forward).setOnClickListener { goForward() }
        findViewById<TextView>(R.id.btn_refresh).setOnClickListener { refresh() }
        findViewById<TextView>(R.id.btn_tabs).setOnClickListener { showMenuOption("Tabs") }
        findViewById<TextView>(R.id.btn_menu).setOnClickListener { showMenu() }

        loadUrl("https://www.google.com")
    }

    private fun loadUrl(input: String) {
        val url = UrlUtils.smartUrlFilter(input)
        webView.loadUrl(url)
        urlBar.setText(url)
        urlBar.clearFocus()
    }

    private fun goBack() {
        if (webView.canGoBack()) webView.goBack()
    }

    private fun goForward() {
        if (webView.canGoForward()) webView.goForward()
    }

    private fun refresh() {
        webView.reload()
    }

    private fun showMenuOption(title: String) {
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show()
    }

    private fun showMenu() {
        val options = arrayOf("Bookmarks", "History", "Settings")
        AlertDialog.Builder(this)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showMenuOption("Bookmarks")
                    1 -> showMenuOption("History")
                    2 -> showMenuOption("Settings")
                }
            }
            .show()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}