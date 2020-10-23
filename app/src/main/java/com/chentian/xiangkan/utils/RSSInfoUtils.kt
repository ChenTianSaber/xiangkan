package com.chentian.xiangkan.utils

import android.util.Log
import com.chentian.xiangkan.R
import com.chentian.xiangkan.db.RSSItem
import com.chentian.xiangkan.db.RSSManagerInfo
import com.chentian.xiangkan.page.main.RSSRepository
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

object RSSInfoUtils {

    private const val TAG = "RSSInfoUtils"

    /**
     * 默认RSS地址全部集合
     */
    var RSSLinkList = mutableListOf(
        RSSManagerInfo(
            link = "https://sspai.com/feed",
            name = "少数派",
            description = "少数派致力于更好地运用数字产品或科学方法，帮助用户提升工作效率和生活品质",
            channelLink = "https://sspai.com",
            showWeb = false
        ),
        RSSManagerInfo(
            link = "https://rsshub.ioiox.com/zhihu/daily",
            name = "知乎日报",
            description = "每天3次，每次7分钟",
            channelLink = "https://daily.zhihu.com",
            showWeb = true
        ),
        RSSManagerInfo(
            link = "https://rsshub.ioiox.com/zhihu/hotlist",
            name = "知乎热榜",
            description = "知乎热榜",
            channelLink = "https://www.zhihu.com/billboard",
            showWeb = false
        ),
    )

    var followRSSLink = mutableSetOf<String>()

    /**
     * 根据channelLink判断是否需要展示网页
     */
    fun isShowWeb(channelLink: String):Boolean{
        for(rssInfo in RSSLinkList){
            if(rssInfo.channelLink == channelLink) return rssInfo.showWeb
        }
        return true
    }

    /**
     * 根据channelLink返回不同的icon
     */
    fun getRSSIcon(channelLink:String): Int {
        if(channelLink == "https://sspai.com"){
            return R.mipmap.icon_sspai
        }else if(channelLink == "https://www.gcores.com"){
            return R.mipmap.icon_jihe
        }else if(channelLink == "https://daily.zhihu.com"){
            return R.mipmap.icon_zhihudaily
        }else if(channelLink.contains("zhihu",ignoreCase = false)){
            return R.mipmap.icon_zhihu
        }else if(channelLink.contains("bilibili",ignoreCase = false)){
            return R.mipmap.icon_bilibili
        }else{
            return R.mipmap.ic_launcher
        }
    }

    /**
     * 检测RSS数据
     */
    fun checkRSSData(link: String, showWeb: Boolean): RSSManagerInfo? {
        Log.d(TAG, "checkRSSData: link-> $link showWeb->$showWeb")
        var rssManagerInfo: RSSManagerInfo? = null
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
            val data: InputStream = connection.inputStream

            //检测数据
            data.use {
                val xmlToJson: XmlToJson = XmlToJson.Builder(data, null).build()
                val jsonObject = xmlToJson.toJson()
                val channelJsonObject = jsonObject?.optJSONObject("rss")?.optJSONObject("channel")
                val jsonArray = channelJsonObject?.optJSONArray("item")

                val channelTitle = channelJsonObject?.optString("title")
                val channelLink = channelJsonObject?.optString("link")
                val channelDescription = channelJsonObject?.optString("description")
                val channelManagingEditor = channelJsonObject?.optString("managingEditor")

                if(channelTitle.isNullOrEmpty() || channelLink.isNullOrEmpty()){
                    Log.d(TAG, "checkRSSData: channelTitle或者channelLink为空")
                    return null
                }
                rssManagerInfo = RSSManagerInfo(
                    link = link,
                    name = channelTitle ?: "",
                    description = channelDescription ?: "",
                    channelLink = channelLink ?: "",
                    showWeb = showWeb
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }

        return rssManagerInfo
    }
}