package com.chentian.xiangkan

import android.annotation.SuppressLint
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

//    private lateinit var contentTextView: TextView
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

//        contentTextView = findViewById(R.id.content)

//        val imgGetter = ImageGetter { source ->
//            var drawable: Drawable? = null
////            val url: URL
////            try {
////                url = URL(source)
////                drawable = Drawable.createFromStream(url.openStream(), "")
////            } catch (e: Exception) {
////                Log.e(TAG, "imgGetter Exception --> $e")
////                return@ImageGetter null
////            }
//            drawable = resources.getDrawable(R.mipmap.ic_launcher)
//            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
//            drawable
//        }
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            contentTextView.text = Html.fromHtml(description, Html.FROM_HTML_OPTION_USE_CSS_COLORS,imgGetter,null)
//        }else{
//            contentTextView.text = Html.fromHtml(description,imgGetter,null)
//        }

        val html = """
            <!DOCTYPE html>
            <html lang="en" id="html">
            <head>
              <meta charset="utf-8">
              <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
              <meta name="fragment" content="!">
              <link rel="stylesheet" href="https://cdn.sspai.com/static/neo/element-ui@2.4.5.css">
              <link rel="stylesheet" href="https://post.sspai.com/sspai-ui@1.17.4/sspai-ui.css">
              <link rel="stylesheet" href="https://cdn.sspai.com/static/js/lightgallery@1.1.3/css/lightgallery.min.css">
            </head>
            <body id="appBody">
                $description
            </body>
            </html>
        """

        webView = findViewById(R.id.web_view)
        webView.settings.javaScriptEnabled = true
//        webView.loadData(description,"text/html", "UTF-8")
        webView.loadData(html,"text/html", "UTF-8")

    }


}