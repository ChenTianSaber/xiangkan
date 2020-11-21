package com.chentian.xiangkan.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.chentian.xiangkan.*
import com.chentian.xiangkan.data.RssItem
import com.chentian.xiangkan.utils.HtmlUtils
import com.chentian.xiangkan.utils.RssUtils
import com.githang.statusbar.StatusBarCompat
import java.text.SimpleDateFormat
import java.util.*

/**
 * 内容页fragment
 * 这个页面和其他的业务应该是独立的，它就是用来展示内容而已
 */
class ReportActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ReportActivity"
    }

    //  region field

    private lateinit var webView: WebView
    private lateinit var backBtn: ImageView

    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_report)
        StatusBarCompat.setStatusBarColor(this, resources.getColor(R.color.white_2), true)

        initView()
        initData()
    }

    private fun initView() {
        webView = findViewById(R.id.web_view)
        backBtn = findViewById(R.id.back_btn)

        backBtn.setOnClickListener {
            if(webView.canGoBack()){
                webView.goBack()
                return@setOnClickListener
            }
            finish()
        }


    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initData() {

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true // 这个要加上
        /* 获得 webview url，请注意url单词是product而不是products，products是旧版本的参数，用错地址将不能成功提交 */
        val url = "https://support.qq.com/product/294473"
        /* WebView 内嵌 Client 可以在APP内打开网页而不是跳出到浏览器 */
        val webViewClient: WebViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                super.shouldOverrideUrlLoading(view, url)
                view.loadUrl(url)
                return true
            }
        }
        webView.webViewClient = webViewClient
        webView.loadUrl(url)

    }

}