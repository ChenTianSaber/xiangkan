package com.chentian.xiangkan.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RSSItem(
    val title: String?,
    val link: String?, //内容页展示的url
    val description: String?,
    val author: String?,
    val pubDate: Long?,
    val channelTitle: String?,
    val channelLink: String?, //主站的url
    val channelDescription: String?,
    val channelManagingEditor: String?,
){
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0
    var wasRead: Boolean? = false
    var imageUrl: String? = ""
}