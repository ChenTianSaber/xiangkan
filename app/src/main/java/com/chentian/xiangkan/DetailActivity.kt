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


class DetailActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "DetailActivity"
    }

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_detail)
        StatusBarCompat.setStatusBarColor(this, Color.WHITE, true)

        val title:String = intent.extras?.get("title") as String
        val author:String = intent.extras?.get("author") as String
        val pubDate:Long = intent.extras?.get("pubDate") as Long
        val link:String = intent.extras?.get("link") as String
        val description:String = intent.extras?.get("description") as String

        val html = buildSSPaiHtml(description)

        webView = findViewById(R.id.web_view)
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