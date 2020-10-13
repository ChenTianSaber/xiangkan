package com.chentian.xiangkan

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.chentian.xiangkan.db.RSSItem

class RSSViewModel(
    rssRepository: RSSRepository
) : ViewModel() {
    val rssItemList: LiveData<MutableList<RSSItem>> = rssRepository.getRSSData()
}