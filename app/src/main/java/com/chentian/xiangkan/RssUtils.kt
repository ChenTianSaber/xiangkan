package com.chentian.xiangkan

object RssUtils{
    /**
     * 根据channelLink返回不同的icon
     */
    fun getRSSIcon(channelLink:String): Int {
        if(channelLink == "https://sspai.com"){
            return R.mipmap.icon_sspai
        }else if(channelLink.contains("zhihu.com",ignoreCase = false)){
            return R.mipmap.icon_zhihu
        }else if(channelLink == "-1"){
            return R.mipmap.quanbu
        }else{
            return R.mipmap.ic_launcher
        }
    }
}