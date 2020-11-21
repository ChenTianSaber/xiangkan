package com.chentian.xiangkan.utils

import android.util.Log
import com.chentian.xiangkan.data.RssItem

/**
 * Html构建工具，为了保证体验，对不同的网站需要进行不同的适配
 */
object HtmlUtils {

    const val TAG = "HtmlUtils"

    fun buildHtml(rssItem: RssItem):String{

//        Log.d(TAG, "buildHtml: ---> ${rssItem.description}")

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