package com.chentian.xiangkan.page.main

import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chentian.xiangkan.db.RSSItem
import com.chentian.xiangkan.db.RSSItemDao
import com.chentian.xiangkan.utils.RSSInfoUtils
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.InputStream
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

class RSSRepository constructor(
    private val rssItemDao: RSSItemDao
) {

    companion object {
        const val TAG = "RSSRepository"
        var latestPubDateMap = mutableMapOf<String,Long>() // <link,time> 通过link来获取最新的更新时间
    }

    /**
     * 获取RSS数据
     */
    fun getRSSData(): LiveData<MutableList<RSSItem>> {
        val rssData = MutableLiveData<MutableList<RSSItem>>()
        GlobalScope.launch(Dispatchers.IO) {
            // 先请求Web数据，然后比对有无更新，有的话将更新的数据插入数据库，再从数据库返回数据，数据库是单一数据源
            // web数据会有多个订阅源，所有源都请求结束后再获取数据
            for(rssManagerInfo in RSSInfoUtils.RSSLinkList){
                requestRSSDate(rssManagerInfo.link)
            }
            rssData.postValue(getRSSDateFromDB())
        }
        Log.d(TAG, "return rssData")
        return rssData
    }

    /**
     * 请求web数据
     */
    private fun requestRSSDate(link:String) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(link)
            connection = url.openConnection() as HttpURLConnection
            //设置请求方法
            connection.requestMethod = "GET"
            //设置连接超时时间（毫秒）
            connection.connectTimeout = 5000
            //设置读取超时时间（毫秒）
            connection.readTimeout = 5000

            //返回输入流
            val inputStream: InputStream = connection.inputStream

            //解析xml数据
            val rssData = parseRSSData(inputStream)
            Log.d(TAG, "requestRSSDate: size --> ${rssData.size}")
            insertDB(rssData)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * 对比数据，然后存储数据库
     */
    private fun insertDB(rssData: MutableList<RSSItem>){
        if(rssData.isNullOrEmpty()){
            return
        }
        var pubDate = latestPubDateMap[rssData[0].channelLink] ?: -1
        if(latestPubDateMap.isNullOrEmpty() || pubDate == -1L
            || rssData[rssData.lastIndex].pubDate!! > pubDate){
            // 说明还没有数据，或者Web数据最老的数据也比目前的数据新，那就直接插入
            rssItemDao.insertAll(rssData)
        }else {
            // 对比数据
            for(rssItem in rssData){
                if(rssItem.pubDate!! <= pubDate){
                    break
                }
                rssItemDao.insertItem(rssItem)
            }
        }
        Log.d(TAG, "insertDB: rssData[0].pubDate --> ${rssData[0].pubDate}")
        pubDate = rssData[0].pubDate ?: pubDate
        // 更新最后时间
        latestPubDateMap[rssData[0].channelLink ?: ""] = pubDate
    }

    /**
     * 从数据库获取数据
     */
    private fun getRSSDateFromDB() : MutableList<RSSItem>{
        val rssData = rssItemDao.getAllOrderByPubDate()
        Log.d(TAG, "getRSSDateFromDB: size --> ${rssData.size}")
        return rssData
    }

    /**
     * 解析RSS数据
     */
    private fun parseRSSData(data: InputStream): MutableList<RSSItem> {
        val dataList = mutableListOf<RSSItem>()

        /**
         * 提取作者的信息，一般都是author，但是知乎这边是dc:creator
         */
        fun getAuthor(json:JSONObject,channelTitle:String?):String{
            var author = json.optString("author")
            try {
                if(author.isNullOrEmpty()){
                    author = json.optJSONObject("dc:creator").optString("content")
                }
            }catch (e:NullPointerException){
                //如果都没有的话，直接用channelTitle代替吧
                author = channelTitle
            }

            Log.d(TAG, "getAuthor: $author")
            return author
        }

        /**
         * 提取时间信息
         */
        fun getTime(json:JSONObject):Long{
            return try {
                Date(json.optString("pubDate")).time
            }catch (e:IllegalArgumentException){
                SimpleDateFormat("yyyy-MM-ddHH:mm:ss").parse(json.optString("pubDate").replace("T","")).time
            }
        }

        data.use {
            val xmlToJson: XmlToJson = XmlToJson.Builder(data, null).build()
            val jsonObject = xmlToJson.toJson()
            val channelJsonObject = jsonObject?.optJSONObject("rss")?.optJSONObject("channel")
            val jsonArray = channelJsonObject?.optJSONArray("item")

            val channelTitle = channelJsonObject?.optString("title")
            val channelLink = channelJsonObject?.optString("link")
            val channelDescription = channelJsonObject?.optString("description")
            val channelManagingEditor = channelJsonObject?.optString("managingEditor")

            if (jsonArray != null) {
                for (i in 0 until jsonArray.length()) {
                    val json = (jsonArray.get(i) as JSONObject)
                    dataList.add(
                        RSSItem(
                            title = json.optString("title"),
                            link = json.optString("link"),
                            description = json.optString("description"),
                            author = getAuthor(json,channelTitle),
                            pubDate = getTime(json),
                            channelTitle = channelTitle,
                            channelLink = channelLink,
                            channelDescription = channelDescription,
                            channelManagingEditor = channelManagingEditor,
                        )
                    )
                }
            }
        }
        return dataList
    }

    /**
     * 更新RSSItem的数据
     */
    fun updateRSSItem(rssItem:RSSItem){
        GlobalScope.launch(Dispatchers.IO) {
            rssItemDao.updateItems(rssItem)
        }
    }

}