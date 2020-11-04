package com.chentian.xiangkan

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 这个类是订阅源的数据
 */
@Entity
data class RssLinkInfo (
        val url:String, //订阅请求的链接
        val channelLink:String, //主站的链接
        val channelTitle:String, //主站名称
        val channelDescription:String, // 主站的描述
        var state:Boolean = false, // 是否开启订阅
        var icon:String = "http://i1.hdslb.com/bfs/face/426f088e9869768b8d5365a09f55b56e44c8f53e.jpg" // 图标
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var latsedTitle: String = "" // 最新
    var latestPubDate: Long = RssRepository.NO_DATD
}