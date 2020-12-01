package com.chentian.xiangkan.utils

import android.content.Context
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chentian.xiangkan.R
import com.chentian.xiangkan.data.RssItem
import com.chentian.xiangkan.data.RssLinkInfo
import com.chentian.xiangkan.data.RssLinkInfoFactory
import com.chentian.xiangkan.repository.RssItemRepository
import com.chentian.xiangkan.repository.RssLinkRepository
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

object RssUtils {

    const val TAG = "RssUtils"

    /**
     * Item展示的ViewType
     */
    const val VIEW_TYPE_TEXT = 0
    const val VIEW_TYPE_IMAGE = 1
    const val VIEW_TYPE_VIDEO = 2

    /**
     * 请求web RssItem数据
     */
    fun requestRssItems(rssLinkInfo: RssLinkInfo): MutableList<RssItem>? {
        Log.d(TAG, "request: url --> ${rssLinkInfo.url}")
        var connection: HttpURLConnection? = null
        try {
            connection = URL(rssLinkInfo.url).openConnection() as HttpURLConnection
            //设置请求方法
            connection.requestMethod = "GET"
            //设置连接超时时间（毫秒）
            connection.connectTimeout = 10000
            //设置读取超时时间（毫秒）
            connection.readTimeout = 10000

            //返回输入流
            val inputStream = connection.inputStream
            return parseRssData(inputStream, rssLinkInfo)

        } catch (e: Exception) {
            Log.d(TAG, "request: Exception --> $e")
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }

        return null
    }

    /**
     * 解析RSS内容数据
     */
    private fun parseRssData(data: InputStream, rssLinkInfo: RssLinkInfo): MutableList<RssItem> {
        val dataList = mutableListOf<RssItem>()

        /**
         * 提取作者的信息，一般都是author，但是知乎这边是dc:creator
         */
        fun getAuthor(json: JSONObject, channelTitle: String?): String {
            var author = json.optString("author")
            try {
                if (author.isNullOrEmpty()) {
                    author = json.optJSONObject("dc:creator").optString("content")
                }
            } catch (e: NullPointerException) {
                //如果都没有的话，直接用channelTitle代替吧
                author = channelTitle
            }

            Log.d(RssItemRepository.TAG, "getAuthor: $author")
            return author
        }

        /**
         * 提取时间信息
         */
        fun getTime(json: JSONObject): Long {
            return try {
                Date(json.optString("pubDate")).time
            } catch (e: java.lang.Exception) {
                Date().time
            }
        }

        /**
         * 解析description获取第一张图片
         */
        fun getImageUrl(json: JSONObject): String {
            val description = json.optString("description")
            val pics: MutableList<String> = ArrayList()
            val compile = Pattern.compile("<img.*?>")
            val matcher: Matcher = compile.matcher(description)
            while (matcher.find()) {
                val img: String = matcher.group()
                pics.add(img)
            }
            if (pics.isNullOrEmpty()) return ""
            val m = Pattern.compile("\"http?(.*?)(\"|>|\\s+)").matcher(pics[0])
            m.find()
            val url = m.group()
            return url.substring(1, url.length - 1)
        }

        fun checkItem(rssItem: RssItem): Boolean{
            if(rssItem.link.isEmpty()){
                return false
            }

            return true
        }

        data.use {
            val xmlToJson: XmlToJson = XmlToJson.Builder(it, null).build()
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
                            author = getAuthor(json, rssLinkInfo.channelTitle),
                            pubDate = getTime(json)
                    )
                    rssItem.imageUrl = getImageUrl(json)
                    rssItem.icon = rssLinkInfo.icon
                    if(checkItem(rssItem)) dataList.add(rssItem)
                }
            }
        }
        return dataList
    }

    /**
     * 添加B站up主动态订阅
     */
    fun addBiliBiliUpDynamic(uid: String): RssLinkInfo {

        val rssLinkInfo = RssLinkInfo()

        /**
         * 获取BiliBili用户的信息
         */
        fun getBiliBiliInfo(uid: String): JSONObject? {
            var bilibiliJson: JSONObject? = null
            var infoConnection: HttpURLConnection? = null
            try {
                val url = URL("${RssLinkInfoFactory.BILIBILI_API}$uid")
                infoConnection = url.openConnection() as HttpURLConnection
                //设置请求方法
                infoConnection.requestMethod = "GET"
                //设置连接超时时间（毫秒）
                infoConnection.connectTimeout = 10000
                //设置读取超时时间（毫秒）
                infoConnection.readTimeout = 10000

                //返回输入流
                val inputStream: InputStream = infoConnection.inputStream

                //解析xml数据
                inputStream.use {
                    val reader = BufferedReader(inputStream.reader())
                    val content = StringBuilder()
                    reader.use {
                        var line = it.readLine()
                        while (line != null) {
                            content.append(line)
                            line = it.readLine()
                        }
                    }
                    bilibiliJson = JSONObject(content.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                infoConnection?.disconnect()
            }

            return bilibiliJson
        }

        /**
         * 先通过接口获取订阅数据，检测是否可以正常获取数据
         */
        fun getChannelData(){
            var connection: HttpURLConnection? = null
            try {
                val url = URL("${RssLinkInfoFactory.BILIBILI_UP}$uid")
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
                inputStream.use {
                    val xmlToJson: XmlToJson = XmlToJson.Builder(inputStream, null).build()
                    val jsonObject = xmlToJson.toJson()
                    val channelJsonObject = jsonObject?.optJSONObject("rss")?.optJSONObject("channel")

                    channelJsonObject?.let { channelData ->
                        Log.d(TAG, "addBiliBiliUpDynamic: channelData --> $channelData")
                        // 在通过接口获取up信息，主要是名字和头像
                        getBiliBiliInfo(uid)?.let { json ->
                            val dataObj = json.optJSONObject("data")
                            Log.d(TAG, "addBiliBiliUpDynamic: dataObj ---> $dataObj")
                            dataObj?.let { data ->
                                rssLinkInfo.channelTitle = data.optString("name")
                                rssLinkInfo.channelDescription = data.optString("sign")
                                rssLinkInfo.url = "${RssLinkInfoFactory.BILIBILI_UP}$uid"
                                rssLinkInfo.channelLink = channelData.optString("link")
                                rssLinkInfo.icon = data.optString("face")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                connection?.disconnect()
            }
        }

        getChannelData()

        Log.d(TAG, "addBiliBiliUpDynamic: rssLinkInfo ---> $rssLinkInfo")
        return rssLinkInfo
    }

    fun setIcon(contex: Context, channelLink: String, view: ImageView) {

        // 看看是不是全部图标
        when (channelLink) {
            "-1" -> {
                Glide.with(contex).load(R.mipmap.quanbu).into(view)
                return
            }
        }

        // 从rssLink里查找
        for (rssLink in RssLinkRepository.rssLinkList) {
            if (rssLink.channelLink == channelLink) {
                Glide.with(contex).load(rssLink.icon).into(view)
                return
            }
        }

        // 设置默认图标
        Glide.with(contex).load(R.mipmap.ic_launcher).into(view)

    }

    /**
     * 根据channelLink判断这个源是否默认展示网页
     */
    fun isShowWeb(channelLink: String): Boolean{
        return when(channelLink){
            "https://www.kaiyanapp.com/" -> true
            "https://www.pingwest.com/status" -> true
            "https://www.ui.cn/" -> true
            "https://chaping.cn/news?cate=" -> true
            "https://www.zcool.com.cn/discover/0!0!0!0!0!!!!2!-1!1" -> true
            "https://www.gcores.com/articles" -> true
            "https://sspai.com" -> true
            "https://www.bilibili.com/h5/weekly-recommend" -> true
            else -> false
        }
    }

    /**
     * 根据channelLink判断这个Item的ViewType
     */
    fun getViewTypeByChannelLink(channelLink: String): Int{
        return when(channelLink){
            "https://www.zcool.com.cn/discover/0!0!0!0!0!!!!2!-1!1" -> VIEW_TYPE_IMAGE
            "https://sspai.com" -> VIEW_TYPE_VIDEO
            else -> VIEW_TYPE_TEXT
        }
    }

}