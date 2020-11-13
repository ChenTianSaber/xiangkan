package com.chentian.xiangkan.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.chentian.xiangkan.data.*
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
     * 内置的直接在代码里，用户的存在数据库里
     * 获取的时候两个都取出来，存入数据库再返回
     * 之所以要这么做是因为对于默认的数据源，也是有订阅状态的
     */
    fun getAllRssLinkInfo(flag: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            Log.d(TAG, "getAllRssLinkInfo start")
            val defaultRssLinks = RssLinkInfoFactory.getDefaultRssLinkInfo()
            for(rssLink in defaultRssLinks){
                if(rssLinkInfoDao.getItemByUrl(rssLink.url).isNullOrEmpty()){
                    rssLinkInfoDao.insertItem(rssLink)
                }
            }

            val allRssLinks = rssLinkInfoDao.getAll()
            rssLinksData.postValue(
                ResponseData(
                    code = flag,
                    data = allRssLinks,
                    message = "获取订阅源数据成功"
                )
            )
        }
    }

    /**
     * 更新订阅源数据
     */
    fun updateRssLinkInfo(rssLinkInfo: RssLinkInfo){
        GlobalScope.launch(Dispatchers.IO) {
            rssLinkInfoDao.updateItems(rssLinkInfo)
        }
    }

    /**
     * 插入订阅源数据
     */
    fun insertRssLinkInfo(rssLinkInfo: RssLinkInfo){
        GlobalScope.launch(Dispatchers.IO) {
            if(rssLinkInfoDao.getItemByUrl(rssLinkInfo.url).isNullOrEmpty()){
                rssLinkInfoDao.insertItem(rssLinkInfo)
                getAllRssLinkInfo(ResponseCode.GET_RSSLINK_SUCCESS_NO_REQUEST)
                Log.d(TAG, "插入订阅源数据 success ---> $rssLinkInfo")
            }
            Log.d(TAG, "插入订阅源数据 end")
        }
    }

}