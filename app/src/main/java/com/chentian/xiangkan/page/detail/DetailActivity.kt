package com.chentian.xiangkan.page.detail

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chentian.xiangkan.R
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
    private lateinit var titleTextView: TextView
    private lateinit var authorTextView: TextView
    private lateinit var pubDateTextView: TextView
    private lateinit var scrollView: ScrollView

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
        StatusBarCompat.setStatusBarColor(this, Color.WHITE, true)

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

        titleTextView.text = title
        authorTextView.text = author

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE)
        val date = Date(pubDate.plus(8 * 60 * 60 * 1000))//加8小时
        pubDateTextView.text = simpleDateFormat.format(date)

        if(showWeb){
            //如果是直接展示网页的话，那么把scrollView隐藏掉，把下面的全屏webView放出来
            scrollView.visibility = View.GONE
            webView = findViewById(R.id.full_web_view)
            webView.visibility = View.VISIBLE
            webView.loadUrl(link)
        }else{
            webView = findViewById(R.id.web_view)
            webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null)
        }

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                Log.d(TAG, "shouldOverrideUrlLoading: ${request?.url}")
                if(request?.url.toString().startsWith("zhihu://")){
                    return true
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }

    }

    private fun buildHtml(description: String):String{
        return if(channelLink == "https://sspai.com"){
            buildSSPaiHtml(description)
        }else if(channelLink == "http://www.zhihu.com"){
            buildZhiHuHtml(description)
        }else if(channelLink.contains("bilibili")){
            buildBilibiliHtml(removeImgIfHasIframe(description))
        }else{
            buildNormalHtml(description)
        }
    }

    private fun removeImgIfHasIframe(description: String):String{
        // 先判断有没有iframe标签，有的话就去掉里面的img标签
        var result:String = description
        val iframeCompile = Pattern.compile("<iframe.*?>")
        val iframeMatcher: Matcher = iframeCompile.matcher(description)
        if(iframeMatcher.find()){
            // 把iframe设置为16:9
            val iframe: String = iframeMatcher.group()
            val list = description.split(iframe)
            result = list[0]+"<div class=\"aspect-ratio\">"+iframe+"</div>"+list[1]

            val compile = Pattern.compile("<img.*?>")
            val matcher: Matcher = compile.matcher(description)
            while (matcher.find()) {
                val img: String = matcher.group()
                result = result.replace(img,"")
            }
        }
        return result
    }

    /**
     * 构造普通的Html
     */
    private fun buildNormalHtml(description: String): String {
        return """
            <!DOCTYPE html>
                <html lang="en" id="html" class="">
                <head>
                    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                    <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
                    <link rel="stylesheet" href="sspai-ui.css" type="text/css">
                </head>
                <body id="appBody">
                    <div data-v-450436a6="" data-v-5d84cf38="" class="article-detail">
                        <article data-v-450436a6="" class="normal-article">
                            <div data-v-b5fe1ab0="" data-v-450436a6="" id="" class="article-body">
                                <div data-v-b5fe1ab0="" class="articleWidth-content">
                                    <div data-v-b5fe1ab0="" class="content wangEditor-txt">
                                        $description
                                    </div>
                                </div>
                            </div>
                        </article>
                        <p/><p/><p/><p/><p/><p/>
                    </div>
                </body>
            </html>
        """
    }

    /**
     * 构造bilibili的Html，加载对应CSS样式
     */
    private fun buildBilibiliHtml(description: String): String {
        return """
            <!DOCTYPE html>
                <html lang="en" id="html" class="">
                <head>
                    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                    <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
                    <link rel="stylesheet" href="sspai-ui.css" type="text/css">
                    <style>
                    .aspect-ratio {
                      position: relative;
                      width: 100%;
                      height: 0;
                      padding-bottom: 56%; /* 高度应该是宽度的56% */
                    }
                    .aspect-ratio iframe {
                      position: absolute;
                      width: 100%;
                      height: 100%;
                      left: 0;
                      top: 0;
                    }
                </style>
                </head>
                <body id="appBody">
                    <div data-v-450436a6="" data-v-5d84cf38="" class="article-detail">
                        <article data-v-450436a6="" class="normal-article">
                            <div data-v-b5fe1ab0="" data-v-450436a6="" id="" class="article-body">
                                <div data-v-b5fe1ab0="" class="articleWidth-content">
                                    <div data-v-b5fe1ab0="" class="content wangEditor-txt">
                                        $description
                                    </div>
                                </div>
                            </div>
                        </article>
                        <p/><p/><p/><p/><p/><p/>
                    </div>
                </body>
            </html>
        """
    }

    /**
     * 构造少数派的Html，加载对应CSS样式
     */
    private fun buildSSPaiHtml(description: String): String {
        return """
            <!DOCTYPE html>
                <html lang="en" id="html" class="">
                <head>
                    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                    <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
                    <link rel="stylesheet" href="sspai-ui.css" type="text/css">
                </head>
                <body id="appBody">
                    <div data-v-450436a6="" data-v-5d84cf38="" class="article-detail">
                        <article data-v-450436a6="" class="normal-article">
                            <div data-v-b5fe1ab0="" data-v-450436a6="" id="" class="article-body">
                                <div data-v-b5fe1ab0="" class="articleWidth-content">
                                    <div data-v-b5fe1ab0="" class="content wangEditor-txt">
                                        $description
                                    </div>
                                </div>
                            </div>
                        </article>
                    </div>
                </body>
            </html>
        """
    }

    /**
     * 构造知乎每日精选的Html，加载对应CSS样式
     */
    private fun buildZhiHuHtml(description: String): String {
        return """
            <!DOCTYPE html>
            <html lang="zh" data-ios="true" data-theme="light" data-react-helmet="data-theme">
            
            <head>
              <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
              <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=0,viewport-fit=cover">
              <link rel="stylesheet" href="zhihu.css" type="text/css">
            </head>
            
            <body>
              <div class="Card AnswerCard">
                <div class="QuestionAnswer-content" tabindex="0">
                  <div class="RichContent RichContent--unescapable">
                    <div class="RichContent-inner"><span class="RichText ztext CopyrightRichText-richText" itemprop="text">
                      $description
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </body>
            
            </html>
        """
    }

}