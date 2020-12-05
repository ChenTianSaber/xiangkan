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

        if(rssItem.channelLink == "https://www.bilibili.com/h5/weekly-recommend"){
            return buildBilibiliHtmlPortal(rssItem)
        }

        if(rssItem.channelLink == "https://sspai.com"){
            return buildSSPaiHtml(rssItem)
        }

        if(rssItem.channelLink == "https://www.zhihu.com/billboard"){
            return buildZhiHuHtml(rssItem)
        }

        return buildCommomHtml(rssItem)

    }

    /**
     * 少数派
     */
    private fun buildSSPaiHtml(rssItem: RssItem): String {
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
                                        ${rssItem.description}
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
     * 知乎
     */
    private fun buildZhiHuHtml(rssItem: RssItem): String {
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
                      ${rssItem.description}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </body>
            
            </html>
        """
    }

    /**
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

        // 把iframe的宽高设置为100%
        var iframe = iframes[0].replace(Regex("width=\"[0-9]*?\""),"width=\"100%\"")
        iframe = iframe.replace(Regex("height=\"[0-9]*?\""),"height=\"100%\"")

        Log.d(TAG, "iframe : ---> 之前：[${iframes[0]}], 转换后：[${iframe}]")

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <style>
                    *{
                        margin: 0;
                        padding: 0;
                    }
                    html,body{
                        width: 100%;
                        height: 100%;
                    }
                </style>
            </head>
            <body>
                $iframe
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