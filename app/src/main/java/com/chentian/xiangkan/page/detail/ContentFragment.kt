package com.chentian.xiangkan.page.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.chentian.xiangkan.*
import com.chentian.xiangkan.data.RssItem

/**
 * 内容页fragment
 * 这个页面和其他的业务应该是独立的，它就是用来展示内容而已
 */
class ContentFragment : Fragment() {

    companion object{
        const val TAG = "ContentFragment"
    }

    private lateinit var itemView: View

    /**
     * 当选择网页模式的时候会展示这个
     */
    private lateinit var fullWebView: WebView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.layout_content,container,false)
        initView()
        initData()
        return itemView
    }

    private fun initView() {
        fullWebView = itemView.findViewById(R.id.full_web_view)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initData() {

        val data: RssItem = arguments?.get("RssItem") as RssItem
        Log.d(TAG, "initData: $data")

        val webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                Log.d(TAG, "shouldOverrideUrlLoading: ${request?.url}")
                if(request?.url.toString().startsWith("zhihu://") || request?.url.toString().startsWith("bilibili://")){
                    return true
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }

        fun setupFullWebview(){
            fullWebView.settings.javaScriptEnabled = true
            fullWebView.settings.domStorageEnabled = true
            fullWebView.settings.blockNetworkImage = false
            fullWebView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            fullWebView.webViewClient = webViewClient

            fullWebView.loadUrl(data.link)
        }

        setupFullWebview()

    }

}