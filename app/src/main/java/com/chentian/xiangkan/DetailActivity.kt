package com.chentian.xiangkan

import android.annotation.SuppressLint
import android.content.ClipDescription
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.text.Html.ImageGetter
import android.util.Log
import android.view.Window
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.githang.statusbar.StatusBarCompat
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class DetailActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "DetailActivity"
    }

    // region field
    private lateinit var webView: WebView
    private lateinit var titleTextView: TextView
    private lateinit var authorTextView: TextView
    private lateinit var pubDateTextView: TextView

    private var html = ""
    private var title = ""
    private var author = ""
    private var pubDate = 0L
    private var link = ""
    private var description = ""
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
        description = intent.extras?.get("description") as String

        html = buildSSPaiHtml(description)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView(){
        titleTextView = findViewById(R.id.title)
        authorTextView = findViewById(R.id.author)
        pubDateTextView = findViewById(R.id.pubDate)
        webView = findViewById(R.id.web_view)

        titleTextView.text = title
        authorTextView.text = author

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE)
        val date = Date(pubDate.plus(8 * 60 * 60 * 1000))//加8小时
        pubDateTextView.text = simpleDateFormat.format(date)

        webView.settings.javaScriptEnabled = true
        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null)
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

}