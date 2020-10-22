package com.chentian.xiangkan.utils

import com.chentian.xiangkan.R
import com.chentian.xiangkan.db.RSSManagerInfo

object RSSInfoUtils {

    /**
     * 默认RSS地址全部集合
     */
    var RSSLinkList = mutableListOf(
        RSSManagerInfo(
            link = "https://sspai.com/feed",
            name = "少数派",
            description = "少数派致力于更好地运用数字产品或科学方法，帮助用户提升工作效率和生活品质",
            channelLink = "https://sspai.com",
            showWeb = false
        ),
        RSSManagerInfo(
            link = "https://www.gcores.com/rss",
            name = "机核",
            description = "不止是游戏",
            channelLink = "https://www.gcores.com",
            showWeb = true
        ),
        RSSManagerInfo(
            link = "https://rsshub.ioiox.com/bilibili/user/dynamic/14110780",
            name = "凉风Kaze",
            description = "凉风Kaze 的 bilibili 动态 - Made with love by RSSHub(https://github.com/DIYgod/RSSHub)",
            channelLink = "https://space.bilibili.com/14110780/dynamic",
            showWeb = false
        ),
    )

    var followRSSLink = mutableSetOf<String>()

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
        if(channelLink == "https://sspai.com"){
            return R.mipmap.icon_sspai
        }else if(channelLink == "https://www.gcores.com"){
            return R.mipmap.icon_jihe
        }else if(channelLink == "http://www.zhihu.com"){
            return R.mipmap.icon_zhihu
        }else if(channelLink.contains("bilibili",ignoreCase = false)){
            return R.mipmap.icon_bilibili
        }else if(channelLink == "https://daily.zhihu.com"){
            return R.mipmap.icon_zhihudaily
        }else{
            return R.mipmap.ic_launcher
        }
    }
}