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
class RssLinkRepository(
        var rssLinksData: MutableLiveData<ResponseData>,
        var rssLinkInfoDao: RssLinkInfoDao
) {

    companion object {
        const val TAG = "RssLinkRepository"
    }

    /**
     * 获取当前所有的订阅源
     * 订阅源分为两种，一种是APP内置的，一种是用户手动创建的
     * 当获取的时候，会先把APP内置的订阅源存进数据库里，然后再统一从数据库中获取所有的订阅源
     */
    suspend fun getAllRssLinkInfo(){
        GlobalScope.launch(Dispatchers.IO) {

        }
    }

}