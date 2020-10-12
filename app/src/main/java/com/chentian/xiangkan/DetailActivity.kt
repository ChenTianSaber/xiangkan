package com.chentian.xiangkan

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.githang.statusbar.StatusBarCompat

class DetailActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "DetailActivity"
    }

//    private lateinit var webView:WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_detail)
        StatusBarCompat.setStatusBarColor(this, Color.WHITE,true)

        val title:String = intent.extras?.get("title") as String
        val author:String = intent.extras?.get("author") as String
        val pubDate:String = intent.extras?.get("pubDate") as String
        val link:String = intent.extras?.get("link") as String
        val description:String = intent.extras?.get("description") as String

//        webView = findViewById(R.id.web_view)
//        webView.settings.javaScriptEnabled = true
//        webView.settings.domStorageEnabled = true
//        webView.loadUrl(url)
    }


}