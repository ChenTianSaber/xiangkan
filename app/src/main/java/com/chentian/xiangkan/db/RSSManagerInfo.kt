package com.chentian.xiangkan.db

/**
 * 订阅管理的数据类
 */
data class RSSManagerInfo(
    val link:String,// feed订阅地址
    val name:String,// 名称
    val description: String, //描述
    val channelLink:String, // 网址
    var state:Boolean, // 订阅状态
    var showWeb:Boolean //是否默认直接展示网页
)