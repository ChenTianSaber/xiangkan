package com.chentian.xiangkan

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.chentian.xiangkan.data.AppDatabase
import com.chentian.xiangkan.data.RSSItem
import com.githang.statusbar.StatusBarCompat
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    // region 变量
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: MyAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val context: Context = this
//    private val db = Room.databaseBuilder(applicationContext,AppDatabase::class.java,"xiangkan").build()
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        StatusBarCompat.setStatusBarColor(this, Color.WHITE,true)

        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapter()
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
        viewAdapter.itemClick = object : ItemClick {
            override fun onItemClick(itemView: View, data: RSSItem) {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("title", data.title)
                intent.putExtra("author", data.author)
                intent.putExtra("pubDate", data.pubDate)
                intent.putExtra("link", data.link)
                intent.putExtra("description", data.description)
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
                    viewAdapter.dataList = rssData
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
    private fun parseRSSData(data: InputStream): MutableList<RSSItem> {
        val dataList = mutableListOf<RSSItem>()
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
                            author = json.optString("author"),
                            pubDate = Date(json.optString("pubDate")).time,
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

    //endregion

    class MyAdapter : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        var dataList: List<RSSItem> = mutableListOf()
        var itemClick: ItemClick? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recycler_view, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.title.text = dataList[position].title
            holder.author.text = dataList[position].author

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINESE)
            val date = dataList[position].pubDate?.plus(8 * 60 * 60 * 1000)?.let { Date(it) }//加8小时
            holder.date.text = simpleDateFormat.format(date)
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

            val title: TextView = itemView.findViewById(R.id.title)
            val author: TextView = itemView.findViewById(R.id.author)
            val date: TextView = itemView.findViewById(R.id.date)
        }
    }

    interface ItemClick {
        fun onItemClick(itemView: View, data: RSSItem)
    }

}