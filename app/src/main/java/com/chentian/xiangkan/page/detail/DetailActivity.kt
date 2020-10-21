package com.chentian.xiangkan.page.detail

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chentian.xiangkan.R
import com.chentian.xiangkan.utils.HtmlHelper
import com.chentian.xiangkan.utils.RSSInfoUtils
import com.githang.statusbar.StatusBarCompat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class DetailActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "DetailActivity"
    }

    // region field
    private lateinit var webView: WebView
    private lateinit var fullWebView: WebView
    private lateinit var titleTextView: TextView
    private lateinit var authorTextView: TextView
    private lateinit var pubDateTextView: TextView
    private lateinit var scrollView: ScrollView
    private lateinit var back: ImageView
    private lateinit var readMode: ImageView

    private var html = ""
    private var title = ""
    private var author = ""
    private var pubDate = 0L
    private var link = ""
    private var channelLink = ""
    private var description = ""
    private var showWeb = true //直接展示原始网页
    // endregion

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_detail)
        StatusBarCompat.setStatusBarColor(this, resources.getColor(R.color.white_3), true)

        initData()
        initView()
    }

    private fun initData(){
        title = intent.extras?.get("title") as String
        author = intent.extras?.get("author") as String
        pubDate = intent.extras?.get("pubDate") as Long
        link = intent.extras?.get("link") as String
        channelLink = intent.extras?.get("channelLink") as String
        description = intent.extras?.get("description") as String
        showWeb = intent.extras?.get("showWeb") as Boolean

        html = buildHtml(description)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView(){
        titleTextView = findViewById(R.id.title)
        authorTextView = findViewById(R.id.author)
        pubDateTextView = findViewById(R.id.pubDate)
        scrollView = findViewById(R.id.scroll_view)
        readMode = findViewById(R.id.read_mode)
        back = findViewById(R.id.back)
        fullWebView = findViewById(R.id.full_web_view)
        webView = findViewById(R.id.web_view)

        back.setOnClickListener {
            val web = if(showWeb) fullWebView else webView
            if (web.canGoBack()) {
                web.goBack()
            }else{
                finish()
            }
        }

        readMode.setOnClickListener {
            showWeb = !showWeb
            setReadMode(showWeb,false)
        }

        titleTextView.text = title
        authorTextView.text = author

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE)
        val date = Date(pubDate.plus(8 * 60 * 60 * 1000))//加8小时
        pubDateTextView.text = simpleDateFormat.format(date)

        setReadMode(showWeb,true)

        val webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                Log.d(TAG, "shouldOverrideUrlLoading: ${request?.url}")
                if(!showWeb || request?.url.toString().startsWith("zhihu://")){
                    return true
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }

        fullWebView.settings.javaScriptEnabled = true
        fullWebView.settings.domStorageEnabled = true
        fullWebView.settings.blockNetworkImage = false
        fullWebView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.blockNetworkImage = false
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        fullWebView.webViewClient = webViewClient
        webView.webViewClient = webViewClient

    }

    private fun setReadMode(showWeb:Boolean,isInit:Boolean){
        if(showWeb){
            readMode.setImageResource(R.mipmap.compass)
            //如果是直接展示网页的话，那么把scrollView隐藏掉，把下面的全屏webView放出来
            scrollView.visibility = View.GONE
            fullWebView.visibility = View.VISIBLE
        }else{
            readMode.setImageResource(R.mipmap.book)
            scrollView.visibility = View.VISIBLE
            fullWebView.visibility = View.GONE
        }
        if(isInit) fullWebView.loadUrl(link)
        if(isInit) webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null)
    }

    private fun buildHtml(description: String):String{
        return if(channelLink == "https://sspai.com"){
            HtmlHelper.buildSSPaiHtml(description)
        }else if(channelLink == "http://www.zhihu.com"){
            HtmlHelper.buildZhiHuHtml(description)
        }else if(channelLink.contains("bilibili")){
            HtmlHelper.buildBilibiliHtml(HtmlHelper.removeImgIfHasIframe(description))
        }else{
            HtmlHelper.buildNormalHtml(description)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val web = if(showWeb) fullWebView else webView
        if (keyCode == KeyEvent.KEYCODE_BACK && web.canGoBack()) {
            web.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}