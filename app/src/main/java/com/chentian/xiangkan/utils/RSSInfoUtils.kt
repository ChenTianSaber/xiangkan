package com.chentian.xiangkan.utils

import com.chentian.xiangkan.R
import com.chentian.xiangkan.db.RSSManagerInfo

object RSSInfoUtils {

    /**
     * 已订阅的RSS地址
     */
    var RSSLinkList = mutableListOf(
        RSSManagerInfo(
            link = "https://sspai.com/feed",
            name = "少数派",
            description = "少数派致力于更好地运用数字产品或科学方法，帮助用户提升工作效率和生活品质",
            channelLink = "https://sspai.com",
            state = false,
            showWeb = false
        ),
        RSSManagerInfo(
            link = "https://www.gcores.com/rss",
            name = "机核",
            description = "不止是游戏",
            channelLink = "https://www.gcores.com",
            state = false,
            showWeb = true
        ),
        RSSManagerInfo(
            link = "https://www.zhihu.com/rss",
            name = "知乎每日精选",
            description = "中文互联网最大的知识平台，帮助人们便捷地分享彼此的知识、经验和见解。",
            channelLink = "http://www.zhihu.com",
            state = false,
            showWeb = true
        ),
    )

    /**
     * 根据channelLink判断是否需要展示网页
     */
    fun isShowWeb(channelLink: String):Boolean{
        for(rssInfo in RSSLinkList){
            if(rssInfo.channelLink == channelLink) return rssInfo.showWeb
        }
        return true
    }

    /**
     * 根据channelLink返回不同的icon
     */
    fun getRSSIcon(channelLink:String): Int {
        return when(channelLink){
            "https://sspai.com" -> R.mipmap.icon_sspai
            "https://www.gcores.com" -> R.mipmap.icon_jihe
            "http://www.zhihu.com" -> R.mipmap.icon_zhihu
            else -> R.mipmap.ic_launcher
        }
    }
}