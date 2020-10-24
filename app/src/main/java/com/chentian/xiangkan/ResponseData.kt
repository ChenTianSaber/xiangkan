package com.chentian.xiangkan

import com.chentian.xiangkan.db.RSSItem

data class ResponseData (
    val code:Int,
    val message:String?,
    val data:MutableList<RSSItem>
)