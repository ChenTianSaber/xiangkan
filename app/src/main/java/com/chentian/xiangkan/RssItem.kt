package com.chentian.xiangkan

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 订阅返回数据的Item
 */
@Entity
data class RssItem (
        val url:String, //订阅请求的链接
        val channelLink:String, //主站的链接
        val channelTitle:String, //主站名称
        val channelDescription:String, // 主站的描述
        val title:String, // 内容标题
        val link:String, // 内容跳转的链接
        val description:String, // 内容详情
        val author:String, // 内容作者
        val pubDate:Long // 内容更新时间
){
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0
    var wasRead: Boolean? = false
    var imageUrl: String? = ""
}