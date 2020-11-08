package com.chentian.xiangkan.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

/**
 * 订阅返回数据的Item
 */
@Parcelize
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
) : Parcelable {
    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0
    @IgnoredOnParcel
    var wasRead: Boolean? = false
    @IgnoredOnParcel
    var imageUrl: String? = ""
    @IgnoredOnParcel
    var icon: String? = ""
}