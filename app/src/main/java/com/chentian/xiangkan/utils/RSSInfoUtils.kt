package com.chentian.xiangkan.utils

import com.chentian.xiangkan.R
import com.chentian.xiangkan.db.RSSManagerInfo

object RSSInfoUtils {

    /**
     * 已订阅的RSS地址
     */
    var RSSLinkList = mutableListOf<RSSManagerInfo>(
        RSSManagerInfo(
            link = "https://sspai.com/feed",
            name = "少数派",
            description = "少数派致力于更好地运用数字产品或科学方法，帮助用户提升工作效率和生活品质",
            webUrl = "https://sspai.com",
            state = false
        )
    )

    /**
     * 根据channelLink返回不同的icon
     */
    fun getRSSIcon(channelLink:String): Int {
        return when(channelLink){
            "https://sspai.com" -> R.mipmap.icon_sspai
            else -> R.mipmap.ic_launcher
        }
    }
}