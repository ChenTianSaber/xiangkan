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
import org.json.JSONObject
import java.io.BufferedReader
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
     * 数据来源单一为数据库
     * 先返回数据库中的内容展示数据，再去请求web数据
     * web数据请求后先存入DB，然后再统一从DB取
     */
    fun getRssItems(rssLinkInfos: MutableList<RssLinkInfo>) {
        GlobalScope.launch(Dispatchers.IO) {
            rssItemsData.postValue(
                    ResponseData(
                            code = ResponseCode.DB_SUCCESS,
                            data = getRssItemsFromDB(),
                            message = "从数据库获取内容成功"
                    )
            )

            var resultCode = ResponseCode.WEB_FAIL
            for (linkInfo in rssLinkInfos) {
                resultCode = getRssItemsFromWeb(linkInfo)
                Log.d(TAG, "getRssItemsFromWeb: resultCode ---> $resultCode")
            }

            rssItemsData.postValue(
                    ResponseData(
                            code = resultCode,
                            data = getRssItemsFromDB(),
                            message = "从数据库获取内容成功"
                    )
            )
        }
    }

    /**
     * 获取单个订阅源中的DB数据
     */
    fun getSingleRssLinkInfoRssItems(rssLinkInfo: RssLinkInfo) {
        GlobalScope.launch(Dispatchers.IO) {
            rssItemsData.postValue(
                    ResponseData(
                            code = ResponseCode.DB_SUCCESS,
                            data = getSingleRssLinkInfoRssItemsFromDB(rssLinkInfo),
                            message = "从数据库获取内容成功"
                    )
            )
        }
    }

    private fun getRssItemsFromDB(): MutableList<RssItem> {
        return rssItemDao.getAll()
    }

    private fun getSingleRssLinkInfoRssItemsFromDB(rssLinkInfo: RssLinkInfo): MutableList<RssItem> {
        return rssItemDao.getAllByUrl(rssLinkInfo.url)
    }

    private fun getRssItemsFromWeb(rssLinkInfo: RssLinkInfo): Int {
        val resultList = RssUtils.requestRssItems(rssLinkInfo)

        if (resultList.isNullOrEmpty()) {
            return ResponseCode.WEB_FAIL
        }

//        rssItemDao.insertAll(resultList)
        // 先查找有无重复数据，重复的话则不处理
        for (data in resultList) {
            if (rssItemDao.getAllByTitleAndAuthor(data.title, data.author).isNullOrEmpty()) {
                rssItemDao.insertItem(data)
            }
        }

        return ResponseCode.WEB_SUCCESS
    }

}