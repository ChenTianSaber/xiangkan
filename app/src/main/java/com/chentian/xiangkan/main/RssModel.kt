package com.chentian.xiangkan.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chentian.xiangkan.data.ResponseData

class RssModel:ViewModel() {
    var rssLinksData: MutableLiveData<ResponseData> = MutableLiveData()
//    var rssItemsData: MutableLiveData<ResponseData> = MutableLiveData()
}