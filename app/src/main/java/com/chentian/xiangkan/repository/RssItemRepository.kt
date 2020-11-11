package com.chentian.xiangkan.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.chentian.xiangkan.data.ResponseData
import com.chentian.xiangkan.data.RssItem
import com.chentian.xiangkan.data.RssLinkInfo
import com.chentian.xiangkan.db.RssItemDao
import com.chentian.xiangkan.db.RssLinkInfoDao
import com.chentian.xiangkan.utils.RssUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * 数据仓库
 * MainActivity通过这个类来操作数据，MainActivity本身不可直接访问数据库
 */
class RssItemRepository(
        var rssItemsData: MutableLiveData<ResponseData>,
        var rssItemDao: RssItemDao
) {

    companion object {
        const val TAG = "RssItemRepository"
    }

    /**
     * 获取已订阅数据源的内容
     */
    fun getRssItems(){

    }

}