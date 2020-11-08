package com.chentian.xiangkan.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chentian.xiangkan.main.RssRepository

/**
 * 这个类是订阅源的数据
 */
@Entity
data class RssLinkInfo (
        var url:String = "", //订阅请求的链接
        var channelLink:String = "", //主站的链接
        var channelTitle:String = "", //主站名称
        var channelDescription:String = "", // 主站的描述
        var state:Boolean = false, // 是否开启订阅
        var icon:String = "http://i1.hdslb.com/bfs/face/426f088e9869768b8d5365a09f55b56e44c8f53e.jpg" // 图标
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var latsedTitle: String = "" // 最新
    var latestPubDate: Long = RssRepository.NO_DATD
}