package com.chentian.xiangkan.page.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.chentian.xiangkan.ResponseData
import com.chentian.xiangkan.db.RSSItem
import com.chentian.xiangkan.page.main.RSSRepository

class RSSViewModel(
    rssRepository: RSSRepository
) : ViewModel() {
//    val rssItemList: LiveData<MutableList<RSSItem>> = rssRepository.getRSSData()
    val rssData: LiveData<ResponseData> = rssRepository.getRSSData()
}