package com.chentian.xiangkan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    // region 变量
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: MyAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val context: Context = this;
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapter()
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
        viewAdapter.itemClick = object : ItemClick {
            override fun onItemClick(itemView: View, data: Item) {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("url", data.link)
                startActivity(intent)
            }
        }

        // 请求数据
        getRSSData()
    }

    //region 方法
    /**
     * 获取RSS数据
     */
    private fun getRSSData() {
        //请求RSS数据
        Thread {
            var connection: HttpURLConnection? = null
            try {
                val url = URL("https://sspai.com/feed")
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
                Log.d(TAG, "getRSSData: $rssData")

                runOnUiThread { //更新列表
                    viewAdapter.dataList = rssData?.items!!
                    viewAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                connection?.disconnect()
            }
        }.start()
    }

    /**
     * 解析RSS数据
     */
    private fun parseRSSData(data: InputStream): Channel? {
        var channel: Channel? = null
        data.use {
            val xmlToJson: XmlToJson = XmlToJson.Builder(data, null).build()
            val jsonObject = xmlToJson.toJson()
            val channelJsonObject = jsonObject?.optJSONObject("rss")?.optJSONObject("channel")
            val jsonArray = channelJsonObject?.optJSONArray("item")
            val items = mutableListOf<Item>()
            if (jsonArray != null) {
                for (i in 0 until jsonArray.length()) {
                    val json = (jsonArray.get(i) as JSONObject)
                    items.add(
                        Item(
                            title = json.optString("title"),
                            link = json.optString("link"),
                            description = json.optString("description"),
                            author = json.optString("author"),
                            pubDate = json.optString("pubDate")
                        )
                    )
                }
            }
            channel = Channel(
                title = channelJsonObject?.optString("title"),
                link = channelJsonObject?.optString("link"),
                description = channelJsonObject?.optString("description"),
                pubDate = channelJsonObject?.optString("pubDate"),
                items = items
            )
        }
        return channel
    }

    //endregion

    class MyAdapter : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        var dataList: List<Item> = mutableListOf()
        var itemClick: ItemClick? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recycler_view, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.textView.text = dataList[position].title
            holder.itemView.tag = position
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            init {
                itemView.setOnClickListener {
                    itemClick?.onItemClick(itemView, dataList[itemView.tag as Int])
                }
            }

            val textView: TextView = itemView.findViewById(R.id.text_view)
        }
    }

    interface ItemClick {
        fun onItemClick(itemView: View, data: Item)
    }

    // region 数据类
    /**
     * 数据类
     */
    data class Channel(
        val title: String?,
        val link: String?,
        val description: String?,
        val pubDate: String?,
        val items: List<Item>?
    )

    data class Item(
        val title: String?,
        val link: String?,
        val description: String?,
        val author: String?,
        val pubDate: String?
    )
    // endregion
}