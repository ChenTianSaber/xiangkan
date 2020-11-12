package com.chentian.xiangkan.repository

import androidx.lifecycle.MutableLiveData
import com.chentian.xiangkan.data.ResponseCode
import com.chentian.xiangkan.data.ResponseData
import com.chentian.xiangkan.data.RssItem
import com.chentian.xiangkan.data.RssLinkInfo
import com.chentian.xiangkan.db.RssItemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
            val rssItems = getRssItemsFromDB()
            rssItemsData.postValue(
                ResponseData(
                    code = ResponseCode.DB_SUCCESS,
                    data = rssItems,
                    message = "从数据库获取内容成功"
                )
            )
            getRssItemsFromWeb()
        }
    }

    private fun getRssItemsFromDB(): MutableList<RssItem> {
        return rssItemDao.getAll()
    }

    private fun getRssItemsFromWeb(): Int {

    }

}