package com.chentian.xiangkan.utils

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.chentian.xiangkan.R
import com.chentian.xiangkan.data.RssItem
import com.chentian.xiangkan.data.RssLinkInfo
import com.chentian.xiangkan.MainActivity
import com.chentian.xiangkan.repository.RssRepository
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

    /**
     * 获取rssRepository
     */
    fun getRssRepository(fragment: Fragment): RssRepository {
        return (fragment.activity as MainActivity).rssRepository
    }

    /**
     * 跳转Fragment
     */
    fun navigateFragment(
        fragment: Fragment,
        toFragment: Fragment,
        arguments: Bundle? = null,
        backStack: String? = ""
    ) {
        navigateFragment(fragment.activity as MainActivity, toFragment, arguments, backStack)
    }

    fun navigateFragment(
            activity: MainActivity,
            toFragment: Fragment,
            arguments: Bundle? = null,
            backStack: String? = ""
    ) {
        val transient = activity.supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_view, toFragment)
            if (!backStack.isNullOrEmpty()) addToBackStack(backStack)
        }
        arguments?.let {
            toFragment.arguments = arguments
        }
        transient.commit()
    }

    /**
     * 获取MainActivity
     */
    fun getActivity(fragment: Fragment): MainActivity {
        return fragment.activity as MainActivity
    }

    /**
     * 根据channelLink返回不同的icon
     */
    fun getRSSIcon(channelLink: String): Int {
        return when {
            channelLink == "https://sspai.com" -> R.mipmap.icon_sspai
            channelLink.contains("zhihu.com", ignoreCase = false) -> R.mipmap.icon_zhihu
            channelLink == "-1" -> R.mipmap.quanbu
            else -> R.mipmap.ic_launcher
        }
    }

    /**
     * 添加B站up主动态订阅
     */
    fun addBiliBiliUpDynamic(uid: String): RssLinkInfo {

        /**
         * 获取BiliBili用户的信息
         */
        fun getBiliBiliInfo(uid: String): JSONObject? {
            var bilibiliJson: JSONObject? = null
            var infoConnection: HttpURLConnection? = null
            try {
                val url = URL("https://api.bilibili.com/x/space/acc/info?mid=$uid")
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
                    reader.use { reader ->
                        var line = reader.readLine()
                        while (line != null) {
                            content.append(line)
                            line = reader.readLine()
                        }
                    }
                    bilibiliJson = JSONObject(content.toString())
//                Log.d(TAG, "getBiliBiliInfo: $bilibiliJson")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                infoConnection?.disconnect()
            }

            return bilibiliJson
        }

        val rssLinkInfo = RssLinkInfo()
        // 先通过接口获取订阅数据
        var connection: HttpURLConnection? = null
        try {
            val url = URL("https://rsshub.ioiox.com/bilibili/user/dynamic/$uid")
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
//                    Log.d(TAG, "addBiliBiliUpDynamic: $channelData")
                    // 在通过接口获取up信息，主要是名字和头像
                    getBiliBiliInfo(uid)?.let { json ->
                        val dataObj = json.optJSONObject("data")
                        dataObj?.let { data ->
                            rssLinkInfo.channelTitle = data.optString("name")
                            rssLinkInfo.channelDescription = data.optString("sign")
                            rssLinkInfo.url = "https://rsshub.ioiox.com/bilibili/user/dynamic/$uid"
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

        return rssLinkInfo
    }

    /**
     * 解析RSS数据
     */
    fun parseRssData(data: InputStream, rssLinkInfo: RssLinkInfo): MutableList<RssItem> {
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

            Log.d(RssRepository.TAG, "getAuthor: $author")
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

}