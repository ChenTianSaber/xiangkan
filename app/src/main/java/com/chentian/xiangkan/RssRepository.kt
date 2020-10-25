package com.chentian.xiangkan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * 数据仓库
 */
class RssRepository(
        var rssLinksData: MutableLiveData<ResponseData>,
        var rssItemsData: MutableLiveData<ResponseData>,
        var rssLinkInfoDao: RssLinkInfoDao,
        var rssItemDao: RssItemDao
) {

    companion object {
        const val TAG = "RssRepository"

        const val DB_SUCCESS = 0
        const val WEB_SUCCESS = 1
        const val SUCCESS = 2
    }

    // 订阅源的链接
    private var rssLinks = mutableListOf<RssLinkInfo>()

    // 获取已订阅的源
    fun getRssLinks() {
        GlobalScope.launch(Dispatchers.IO) {
            val defaultList = getDefaultRssLinks()
            val dbList = getRssLinksFromDB()
            rssLinks.clear()
            rssLinks.addAll(defaultList)
            rssLinks.addAll(dbList)
            rssLinksData.postValue(ResponseData(
                    code = SUCCESS,
                    data = rssLinks,
                    message = "成功"
            ))
        }
    }

    // 获取数据里的订阅的源（用户自己存的）
    private fun getRssLinksFromDB():MutableList<RssLinkInfo>{
        return rssLinkInfoDao.getAll()
    }

    /**
     * 构建默认的订阅源
     */
    private fun getDefaultRssLinks():MutableList<RssLinkInfo>{
        return mutableListOf(
                RssLinkInfo(
                        url = "https://sspai.com/feed",
                        channelLink = "https://sspai.com",
                        channelTitle = "少数派",
                        channelDescription = "少数派致力于更好地运用数字产品或科学方法，帮助用户提升工作效率和生活品质",
                        state = true
                )
        )
    }

    // 获取内容列表
    fun getRssItemList() {
        GlobalScope.launch(Dispatchers.IO) {
            //先从数据库将本地数据返回展示，再去取web数据
            var resultList = rssItemDao.getAllOrderByPubDate()
            rssItemsData.postValue(ResponseData(
                    code = DB_SUCCESS,
                    data = resultList,
                    message = "成功"
            ))

            // 先请求Web数据，然后比对有无更新，有的话将更新的数据插入数据库，再从数据库返回数据，数据库是单一数据源
            // web数据会有多个订阅源，所有源都请求结束后再获取数据
            for (rssLinkInfo in rssLinks) {
                if(rssLinkInfo.state) requestRSSData(rssLinkInfo)
            }
            resultList = rssItemDao.getAllOrderByPubDate()
            rssItemsData.postValue(ResponseData(
                    code = WEB_SUCCESS,
                    data = resultList,
                    message = "成功"
            ))
        }
    }

    /**
     * 请求web数据
     */
    private fun requestRSSData(rssLinkInfo:RssLinkInfo) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(rssLinkInfo.url)
            connection = url.openConnection() as HttpURLConnection
            //设置请求方法
            connection.requestMethod = "GET"
            //设置连接超时时间（毫秒）
            connection.connectTimeout = 10000
            //设置读取超时时间（毫秒）
            connection.readTimeout = 10000

            //返回输入流
            val inputStream: InputStream = connection.inputStream

            //解析xml数据
            val rssData = parseRssData(inputStream,rssLinkInfo)
            Log.d(TAG, "requestRSSDate: size --> ${rssData.size}")
            setRssItemsDB(rssData)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * 对比数据，然后存储数据库
     */
    private fun setRssItemsDB(rssData: MutableList<RssItem>){
        if(rssData.isNullOrEmpty()){
            return
        }
        rssItemDao.insertAll(rssData)
    }

    /**
     * 解析RSS数据
     */
    private fun parseRssData(data: InputStream,rssLinkInfo:RssLinkInfo): MutableList<RssItem> {
        val dataList = mutableListOf<RssItem>()

        /**
         * 提取作者的信息，一般都是author，但是知乎这边是dc:creator
         */
        fun getAuthor(json: JSONObject, channelTitle:String?):String{
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
        fun getTime(json: JSONObject):Long{
            return try {
                Date(json.optString("pubDate")).time
            }catch (e:java.lang.Exception){
                Date().time
            }
        }

        /**
         * 解析description获取第一张图片
         */
        fun getImageUrl(json: JSONObject):String{
            val description = json.optString("description")
            val pics: MutableList<String> = ArrayList()
            val compile = Pattern.compile("<img.*?>")
            val matcher: Matcher = compile.matcher(description)
            while (matcher.find()) {
                val img: String = matcher.group()
                pics.add(img)
            }
            if(pics.isNullOrEmpty()) return ""
            val m = Pattern.compile("\"http?(.*?)(\"|>|\\s+)").matcher(pics[0])
            m.find()
            val url = m.group()
            return url.substring(1, url.length - 1)
        }

        data.use {
            val xmlToJson: XmlToJson = XmlToJson.Builder(data, null).build()
            val jsonObject = xmlToJson.toJson()
            val channelJsonObject = jsonObject?.optJSONObject("rss")?.optJSONObject("channel")
            val jsonArray = channelJsonObject?.optJSONArray("item")

            if (jsonArray != null) {
                for (i in 0 until jsonArray.length()) {
                    val json = (jsonArray.get(i) as JSONObject)
                    val rssItem = RssItem(
                            url = rssLinkInfo.url,
                            channelLink = rssLinkInfo.channelLink,
                            channelTitle = rssLinkInfo.channelTitle,
                            channelDescription = rssLinkInfo.channelDescription,
                            title = json.optString("title"),
                            link = json.optString("link"),
                            description = json.optString("description"),
                            author = getAuthor(json,rssLinkInfo.channelTitle),
                            pubDate = getTime(json)
                    )
                    rssItem.imageUrl = getImageUrl(json)
                    dataList.add(rssItem)
                }
            }
        }
        return dataList
    }

}