package com.chentian.xiangkan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RssModel:ViewModel() {
    var rssLinksData: MutableLiveData<ResponseData> = MutableLiveData()
    var rssItemsData: MutableLiveData<ResponseData> = MutableLiveData()
}