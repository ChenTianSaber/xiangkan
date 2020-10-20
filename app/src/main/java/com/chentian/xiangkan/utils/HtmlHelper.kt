package com.chentian.xiangkan.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

object HtmlHelper {

    fun removeImgIfHasIframe(description: String):String{
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
    fun buildNormalHtml(description: String): String {
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
    fun buildBilibiliHtml(description: String): String {
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
    fun buildSSPaiHtml(description: String): String {
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
    fun buildZhiHuHtml(description: String): String {
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