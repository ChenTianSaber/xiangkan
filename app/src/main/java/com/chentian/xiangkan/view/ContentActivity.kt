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
class ContentActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ContentFragment"
    }

    //  region field

    /**
     * 当选择网页模式的时候会展示这个
     */
    private lateinit var fullWebView: WebView

    /**
     * 当选择文本模式的时候会展示下面的控件
     */
    private lateinit var scrollView: ScrollView
    private lateinit var title: TextView
    private lateinit var author: TextView
    private lateinit var pubDate: TextView
    private lateinit var webView: WebView
    private lateinit var icon: ImageView

    private lateinit var backBtn: ImageView
    private lateinit var modeBtn: ImageView

    private var isTextMode: Boolean = true

    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_content)
        StatusBarCompat.setStatusBarColor(this, resources.getColor(R.color.white_2), true)

        initView()
        initData()
    }

    private fun initView() {
        fullWebView = findViewById(R.id.full_web_view)

        scrollView = findViewById(R.id.scroll_view)
        title = findViewById(R.id.title)
        author = findViewById(R.id.author)
        pubDate = findViewById(R.id.pubDate)
        webView = findViewById(R.id.web_view)
        icon = findViewById(R.id.icon)

        backBtn = findViewById(R.id.back_btn)
        modeBtn = findViewById(R.id.mode_btn)

        backBtn.setOnClickListener {
            finish()
        }

        modeBtn.setOnClickListener {
            if (isTextMode) {
                // 这个时候转为网页模式
                fullWebView.visibility = View.VISIBLE
                scrollView.visibility = View.GONE
                Glide.with(this).load(R.mipmap.compass).into(modeBtn)
            } else {
                // 这个时候转为文本模式
                fullWebView.visibility = View.GONE
                scrollView.visibility = View.VISIBLE
                Glide.with(this).load(R.mipmap.book).into(modeBtn)
            }
            isTextMode = !isTextMode
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initData() {

        val data: RssItem = intent.getParcelableExtra<RssItem>("RssItem") as RssItem
        Log.d(TAG, "initData: $data")

        isTextMode = !RssUtils.isShowWeb(data.channelLink)
        if (isTextMode) {
            // 文本模式
            fullWebView.visibility = View.GONE
            scrollView.visibility = View.VISIBLE
            Glide.with(this).load(R.mipmap.book).into(modeBtn)
        } else {
            // 网页模式
            fullWebView.visibility = View.VISIBLE
            scrollView.visibility = View.GONE
            Glide.with(this).load(R.mipmap.compass).into(modeBtn)
        }

        val webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
            ): Boolean {
                Log.d(TAG, "shouldOverrideUrlLoading: ${request?.url}")
                if (request?.url.toString().startsWith("zhihu://") || request?.url.toString().startsWith("bilibili://")) {
                    return true
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }

        fun setupFullWebviewMode() {
            fullWebView.settings.javaScriptEnabled = true
            fullWebView.settings.domStorageEnabled = true
            fullWebView.settings.blockNetworkImage = false
            fullWebView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            fullWebView.webViewClient = webViewClient

            fullWebView.loadUrl(data.link)
        }

        fun setupTextMode() {
            title.text = data.title
            author.text = data.author

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE)
            val date = Date(data.pubDate)
            pubDate.text = simpleDateFormat.format(date)

//            Glide.with(this).load(data.icon).into(icon)
            RssUtils.setIcon(this, data.channelLink, icon)

            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
            webView.settings.blockNetworkImage = false
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            webView.webViewClient = webViewClient

            webView.loadDataWithBaseURL("file:///android_asset/", HtmlUtils.buildHtml(data), "text/html", "UTF-8", null)
        }

        setupFullWebviewMode()
        setupTextMode()

    }

}