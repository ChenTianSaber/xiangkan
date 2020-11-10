package com.chentian.xiangkan.data

data class ResponseData(
        val code: Int, // 返回code
        val data: Any, // 具体数据
        val message: String // 错误信息
)