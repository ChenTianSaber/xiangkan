package com.chentian.xiangkan.page.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.chentian.xiangkan.*
import com.chentian.xiangkan.db.AppDatabase
import com.chentian.xiangkan.db.RSSItem
import com.chentian.xiangkan.page.detail.DetailActivity
import com.chentian.xiangkan.page.manager.ManagerActivity
import com.chentian.xiangkan.utils.RSSInfoUtils
import com.githang.statusbar.StatusBarCompat
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    // region field
    private lateinit var recyclerView: RecyclerView
    private lateinit var addImageView: ImageView

    private lateinit var viewAdapter: RSSListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val context: Context = this
    private lateinit var rssViewModel: RSSViewModel
    private lateinit var sharedPreferences:SharedPreferences
    private lateinit var editor:SharedPreferences.Editor
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        StatusBarCompat.setStatusBarColor(this, Color.WHITE,true)

        initView()
        initData()

    }

    private fun initView(){
        viewManager = LinearLayoutManager(this)
        viewAdapter = RSSListAdapter()
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
        viewAdapter.itemClick = object :
            ItemClick {
            override fun onItemClick(itemView: View, data: RSSItem) {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("title", data.title)
                intent.putExtra("author", data.author)
                intent.putExtra("pubDate", data.pubDate)
                intent.putExtra("link", data.link)
                intent.putExtra("description", data.description)
                intent.putExtra("showWeb", RSSInfoUtils.isShowWeb(data.channelLink ?: ""))
                startActivity(intent)
            }
        }
        addImageView = findViewById(R.id.add)
        addImageView.setOnClickListener {
            startActivity(Intent(this, ManagerActivity::class.java))
        }
    }

    private fun initData(){
        // 获取最新的时间
        sharedPreferences = getSharedPreferences("rss_info", MODE_PRIVATE)
        editor = sharedPreferences.edit()
        val latestPubDateStr = sharedPreferences.getString("latestPubDateMap","{}")
        Log.d(TAG, "initData latestPubDateStr ---> $latestPubDateStr")
        parseStringToMap(latestPubDateStr!!)

        val db = Room.databaseBuilder(applicationContext,AppDatabase::class.java,"xiangkan").build()
        rssViewModel = RSSViewModel(
            RSSRepository(db.rssItemDao())
        )
        //观察数据
        rssViewModel.rssItemList.observe(this){
            // 更新
//            Log.d(TAG, "onCreate: rssViewModel.rssItemList.observe $it")
            viewAdapter.dataList = it
            viewAdapter.notifyDataSetChanged()
            // 这个时候lastestPubDateMap更新了，记下最新的时间
            Log.d(TAG, "rssViewModel.rssItemList.observe ---> ${RSSRepository.latestPubDateMap}")
            editor.putString("latestPubDateMap", RSSRepository.latestPubDateMap.toString())
            editor.commit()
        }
    }

    private fun parseStringToMap(pubDateMapString:String){
        if(pubDateMapString == "{}") return
        val string = pubDateMapString.substring(1,pubDateMapString.length - 1)
        val list = string.trim().split(",")
        for(str in list){
            val mapList = str.trim().split("=")
            RSSRepository.latestPubDateMap[mapList[0]] = mapList[1].toLong()
        }
        Log.d(TAG, "parseStringToMap ---> ${RSSRepository.latestPubDateMap}")
    }

    interface ItemClick {
        fun onItemClick(itemView: View, data: RSSItem)
    }

}