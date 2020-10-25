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
        var state:Boolean = false // 是否开启订阅
){
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0
}