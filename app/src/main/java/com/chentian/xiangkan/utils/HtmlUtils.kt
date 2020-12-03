package com.chentian.xiangkan.utils

import android.util.Log
import com.chentian.xiangkan.data.RssItem
import com.chentian.xiangkan.data.RssLinkInfoFactory
import com.chentian.xiangkan.data.RssLinkInfoFactory.RSSHUB_DOMAIN
import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Html构建工具，为了保证体验，对不同的网站需要进行不同的适配
 */
object HtmlUtils {

    const val TAG = "HtmlUtils"

    fun buildHtml(rssItem: RssItem):String{

        Log.d(TAG, "rssItem: ---> [$rssItem]")

        return buildCommomHtml(rssItem)

    }

    /**
     * 非全屏状态下
     * 当判断是B站的时候使用这个css
     */
    private fun buildBilibiliHtmlPortal(rssItem: RssItem): String {

        val iframes: MutableList<String> = ArrayList()
        val compile = Pattern.compile("<iframe.*?></iframe>")
        val matcher: Matcher = compile.matcher(rssItem.description)
        while (matcher.find()) {
            val img: String = matcher.group()
            iframes.add(img)
        }

        Log.d(TAG, "iframes : ---> [${iframes[0]}]")

        return """
            <!DOCTYPE html>
            <html>

            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <link rel="stylesheet" type="text/css" href="app.css">
                
                <style>
                    /* 这个规则规定了iframe父元素容器的尺寸，我们要去它的宽高比应该是 25:14 */
                    .aspect-ratio {
                        position: relative;
                        width: 100%;
                        height: 0;
                        padding-bottom: 56%; /* 高度应该是宽度的56% */
                    }                   
                    /* 设定iframe的宽度和高度，让iframe占满整个父元素容器 */
                    .aspect-ratio iframe {
                        position: absolute;
                        width: 100%;
                        height: 100%;
                        left: 0;
                        top: 0;
                    }
                </style>
            </head>

            <body>
                <div class="rss-wrapper">
                    <div class="content">
                        <div>
                            <div class="aspect-ratio">
                              ${iframes[0]}
                            </div>
                        </div>
                    </div>
                </div>
            </body>

            </html>
        """.trimIndent()
    }

    /**
     * 通用的html返回（目前使用的是锤子阅读的css）
     */
    private fun buildCommomHtml(rssItem: RssItem): String {
        return """
            <!DOCTYPE html>
            <html>

            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <link rel="stylesheet" type="text/css" href="app.css">
            </head>

            <body onload="onLoaded()">
                <div class="rss-wrapper">
                    <div class="content">
                        <div>
                            ${rssItem.description}
                        </div>
                    </div>
                </div>
            </body>

            </html>
        """.trimIndent()
    }

}