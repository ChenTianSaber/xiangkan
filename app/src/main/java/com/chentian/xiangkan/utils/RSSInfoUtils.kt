package com.chentian.xiangkan.utils

import com.chentian.xiangkan.R

object RSSInfoUtils {

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