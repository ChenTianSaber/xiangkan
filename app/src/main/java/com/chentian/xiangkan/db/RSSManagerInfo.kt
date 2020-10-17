package com.chentian.xiangkan.db

/**
 * 订阅管理的数据类
 */
data class RSSManagerInfo(
    val link:String,
    val name:String,
    val description: String,
    val webUrl:String,
    var state:Boolean,
    var showWeb:Boolean //是否默认直接展示网页
)