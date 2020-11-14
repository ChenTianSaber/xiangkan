package com.chentian.xiangkan.data

data class ResponseData(
        val code: Int, // 返回code
        val data: Any, // 具体数据
        val message: String // 错误信息
)

object ResponseCode{
        const val WEB_SUCCESS = 1
        const val DB_SUCCESS = 2

        const val GET_RSSLINK_SUCCESS_NEED_REQUEST = 13 // 获取Rsslink订阅源数据成功，并且需要请求web数据
        const val GET_RSSLINK_SUCCESS_NO_REQUEST = 14 // 获取Rsslink订阅源数据成功，不需要请求数据
        const val GET_RSSLINK_SUCCESS_NEED_REQUEST_DB = 15 // 获取Rsslink订阅源数据成功，并且需要请求DB数据

        const val WEB_FAIL = 25 // 请求RssItem内容失败
        const val WEB_FAIL_EMPTY = 26 // 内容为空

        const val UPDATE_RSSITEM = 31 // 更新单个RssItem
}