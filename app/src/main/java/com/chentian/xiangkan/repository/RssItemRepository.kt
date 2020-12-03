package com.chentian.xiangkan.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.chentian.xiangkan.data.ResponseCode
import com.chentian.xiangkan.data.ResponseData
import com.chentian.xiangkan.data.RssItem
import com.chentian.xiangkan.data.RssLinkInfo
import com.chentian.xiangkan.db.RssItemDao
import com.chentian.xiangkan.utils.RssUtils
import kotlinx.android.synthetic.main.item_contentlist.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/**
 * 数据仓库
 * MainActivity通过这个类来操作数据，MainActivity本身不可直接访问数据库
 */
class RssItemRepository(
        var rssItemDao: RssItemDao
) {

    companion object {
        const val TAG = "RssItemRepository"

        const val SORT_ALL = 0 // 全部
        const val SORT_UNREAD = 1 // 未读
        const val SORT_READ = 2 // 已读
        var SORT_TYPE = SORT_ALL

        val rssLinkLastRequest = mutableMapOf<String, Long>()
        const val REQUEST_SPACE_TIME = 4 * 60 *60 * 1000

    }

    /**
     * 获取已订阅数据源的内容
     * 数据来源单一为数据库
     * 先返回数据库中的内容展示数据，再去请求web数据
     * web数据请求后先存入DB，然后再统一从DB取
     *
     * update:这边需要一个map记录每个源上次请求的时间，频率默认为4小时请求一次，4小时之内的重复请求则忽略
     */
    fun getRssItems(rssLinkInfos: MutableList<RssLinkInfo>) {
        GlobalScope.launch(Dispatchers.IO) {
            EventBus.getDefault().post(ResponseData(
                code = ResponseCode.DB_SUCCESS,
                data = getRssItemsFromDB(),
                message = "从数据库获取内容成功",
                tag = ResponseCode.ALL
            ))

            var resultCode = ResponseCode.WEB_SUCCESS
            for (linkInfo in rssLinkInfos) {
                if(rssLinkLastRequest[linkInfo.url] != null && (Date().time - rssLinkLastRequest[linkInfo.url]!!) < REQUEST_SPACE_TIME){
                    Log.d(TAG, "四小时之内请求过，现在跳过 url ---> ${linkInfo.url}")
                    continue
                }

                resultCode = getRssItemsFromWeb(linkInfo)
                Log.d(TAG, "getRssItemsFromWeb: resultCode ---> $resultCode")
                EventBus.getDefault().post(
                    ResponseData(
                        code = resultCode,
                        data = getSingleRssLinkInfoRssItemsByDateFromDB(linkInfo),
                        message = "从网络请求完成",
                        tag = ResponseCode.SINGLE
                    )
                )
            }

            ResponseData(
                code = if(resultCode == ResponseCode.WEB_FAIL) ResponseCode.WEB_FAIL else ResponseCode.WEB_SUCCESS,
                data = getRssItemsFromDB(),
                message = "从网络请求完成",
                tag = ResponseCode.ALL
            )
        }
    }

    /**
     * 获取单个订阅源中的DB数据并发送数据回调
     */
    fun getSingleRssLinkInfoRssItems(rssLinkInfo: RssLinkInfo) {
        GlobalScope.launch(Dispatchers.IO) {
            EventBus.getDefault().post(
                ResponseData(
                    code = ResponseCode.DB_SUCCESS,
                    data = getSingleRssLinkInfoRssItemsByDateFromDB(rssLinkInfo),
                    message = "从数据库获取内容成功",
                    tag = ResponseCode.SINGLE
                )
            )
        }
    }

    /**
     * 获取所有订阅源中的DB数据并发送数据回调
     */
    fun getRssLinkInfoRssItems() {
        GlobalScope.launch(Dispatchers.IO) {
            EventBus.getDefault().post(
                ResponseData(
                    code = ResponseCode.DB_SUCCESS,
                    data = getRssItemsFromDB(),
                    message = "从数据库获取内容成功",
                    tag = ResponseCode.ALL
                )
            )
        }
    }

    /**
     * 获取数据库中所有内容数据
     */
    private fun getRssItemsFromDB(): MutableList<RssItem> {
        return when(SORT_TYPE){
            SORT_ALL -> rssItemDao.getAll()
            SORT_READ -> rssItemDao.getAllWasRead()
            SORT_UNREAD -> rssItemDao.getAllUnRead()
            else -> rssItemDao.getAll()
        }
    }

    /**
     * 获取单个订阅源中的DB数据
     */
    private fun getSingleRssLinkInfoRssItemsFromDB(rssLinkInfo: RssLinkInfo): MutableList<RssItem> {
        return rssItemDao.getAllByUrl(rssLinkInfo.url)
    }

    /**
     * 获取单个订阅源中的DB数据
     */
    private fun getSingleRssLinkInfoRssItemsByDateFromDB(rssLinkInfo: RssLinkInfo): MutableList<RssItem> {
        return when(SORT_TYPE){
            SORT_ALL -> rssItemDao.getAllByUrlOrderByPubDate(rssLinkInfo.url)
            SORT_UNREAD -> rssItemDao.getAllByUrlOrderByPubDateUnRead(rssLinkInfo.url)
            SORT_READ -> rssItemDao.getAllByUrlOrderByPubDateWasRead(rssLinkInfo.url)
            else -> rssItemDao.getAllByUrlOrderByPubDate(rssLinkInfo.url)
        }
    }

    private fun getRssItemsFromWeb(rssLinkInfo: RssLinkInfo): Int {
        val resultList = RssUtils.requestRssItems(rssLinkInfo)

        if (resultList.isNullOrEmpty()) {
            return ResponseCode.WEB_FAIL
        }

        // 先查找有无重复数据，重复的话则不处理
        // 这里应该倒着存，这样就是时间逆序的
        for (index in resultList.size - 1 downTo 0){
            val data = resultList[index]
            if (rssItemDao.getAllByTitleAndAuthor(data.title, data.author).isNullOrEmpty()) {
                rssItemDao.insertItem(data)
            }
        }

        // 记录下请求时间
        rssLinkLastRequest[rssLinkInfo.url] = Date().time

        return ResponseCode.WEB_PROGRESS_SUCCESS
    }

    /**
     * 更新RssItem
     */
    fun updateRssItem(rssItem: RssItem){
        GlobalScope.launch(Dispatchers.IO) {
            rssItemDao.updateItems(rssItem)
            EventBus.getDefault().post(
                ResponseData(
                    code = ResponseCode.UPDATE_RSSITEM,
                    data = mutableListOf(rssItem),
                    message = "更新单个RssItem"
                )
            )
        }
    }

}