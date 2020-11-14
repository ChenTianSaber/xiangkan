package com.chentian.xiangkan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 这个类是订阅源的数据
 */
@Entity
data class RssLinkInfo(
    var url: String = "", //订阅请求的链接
    var channelLink: String = "", //主站的链接
    var channelTitle: String = "", //主站名称
    var channelDescription: String = "", // 主站的描述
    var state: Boolean = false, // 是否开启订阅
    var icon: String = "" // 图标
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var latsedTitle: String = "" // 最新
    var latestPubDate: Long = 0
    var isRefreshing: Boolean = false
}

object RssLinkInfoFactory{

    /**
     * BiliBili UP的动态订阅链接
     */
    const val BILIBILI_UP = "https://rsshub.ioiox.com/bilibili/user/dynamic/"
    const val BILIBILI_API = "https://api.bilibili.com/x/space/acc/info?mid="

    /**
     * 全部TAB的占位数据
     */
    const val ALLDATA = "-1"

    /**
     * 构造默认的订阅源数据
     */
    fun getDefaultRssLinkInfo(): MutableList<RssLinkInfo> {
        return mutableListOf(
            RssLinkInfo( // 少数派
                url = "https://sspai.com/feed",
                channelLink = "https://sspai.com",
                channelTitle = "少数派",
                channelDescription = "少数派致力于更好地运用数字产品或科学方法，帮助用户提升工作效率和生活品质",
                state = true
            ),
            RssLinkInfo( // 知乎热榜
                url = "https://rsshub.ioiox.com/zhihu/hotlist",
                channelLink = "https://www.zhihu.com/billboard",
                channelTitle = "知乎热榜",
                channelDescription = "知乎热榜",
                state = true
            )
        )
    }

}