package com.chentian.xiangkan.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chentian.xiangkan.repository.RssRepository

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
    var latestPubDate: Long = RssRepository.NO_DATD
}