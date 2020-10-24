package com.chentian.xiangkan.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 订阅管理的数据类
 */
@Entity
data class RSSManagerInfo(
    val link:String,// feed订阅地址
    val name:String,// 名称
    val description: String, //描述
    val channelLink:String, // 主站的地址
    var showWeb:Boolean //是否默认直接展示网页
){
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0
}