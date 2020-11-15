package com.chentian.xiangkan.utils

import android.util.Log
import com.chentian.xiangkan.R
import com.chentian.xiangkan.data.RssItem
import com.chentian.xiangkan.data.RssLinkInfo
import com.chentian.xiangkan.data.RssLinkInfoFactory
import com.chentian.xiangkan.repository.RssItemRepository
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
                    dataList.add(rssItem)
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

    /**
     * 根据channelLink返回不同的icon
     */
    fun getRSSIcon(channelLink: String): Int {
        return when (channelLink) {
            "-1" -> R.mipmap.quanbu
            else -> R.mipmap.ic_launcher
        }
    }

    /**
     * 根据channelLink判断这个源是否默认展示网页
     */
    fun isShowWeb(channelLink: String): Boolean{
        return when(channelLink){
            "https://www.kaiyanapp.com/" -> true
            "https://www.pingwest.com/status" -> true
            else -> false
        }
    }

}