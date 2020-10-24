package com.chentian.xiangkan.page.main

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chentian.xiangkan.MyEventBus
import com.chentian.xiangkan.R
import com.chentian.xiangkan.db.AppDatabase
import com.chentian.xiangkan.db.RSSItem
import com.chentian.xiangkan.db.RSSManagerInfo
import com.chentian.xiangkan.page.detail.DetailActivity
import com.chentian.xiangkan.page.manager.EventListener
import com.chentian.xiangkan.page.manager.ManagerActivity
import com.chentian.xiangkan.utils.RSSInfoUtils
import com.githang.statusbar.StatusBarCompat

class MainActivity : AppCompatActivity() ,SwipeRefreshLayout.OnRefreshListener,EventListener{

    companion object {
        private const val TAG = "MainActivity"
        const val BACK_FROM_DETAIL = 1000 //从详情页回来
    }

    // region field
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var addImageView: ImageView
    private lateinit var updateMessage: TextView
    private lateinit var sort: ImageView
    private lateinit var refresh: ImageView
    private lateinit var notice: ImageView
    private lateinit var emptyLayout: ConstraintLayout

    private lateinit var viewAdapter: RSSListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val context: Context = this
    private lateinit var rssViewModel: RSSViewModel
    private lateinit var sharedPreferences:SharedPreferences
    private lateinit var editor:SharedPreferences.Editor
    private lateinit var rssRepository: RSSRepository
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        StatusBarCompat.setStatusBarColor(this, resources.getColor(R.color.white_3), true)

        MyEventBus.register(this)
        initView()
        initData()

    }

    private fun initView(){
        emptyLayout = findViewById(R.id.empty_layout)
        viewManager = LinearLayoutManager(this)
        viewAdapter = RSSListAdapter()
        viewAdapter.context = this
        swipeRefreshLayout = findViewById(R.id.swiperefresh)
        swipeRefreshLayout.setOnRefreshListener(this)
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
        viewAdapter.itemClick = object :
            ItemClick {
            override fun onItemClick(itemView: View, data: RSSItem) {
                //点击之后置为已读，并存入数据库
                data.wasRead = true
                viewAdapter.notifyDataSetChanged()
                rssRepository.updateRSSItem(data)
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("title", data.title)
                intent.putExtra("author", data.author)
                intent.putExtra("pubDate", data.pubDate)
                intent.putExtra("link", data.link)
                intent.putExtra("channelLink", data.channelLink)
                intent.putExtra("description", data.description)
                intent.putExtra("showWeb", RSSInfoUtils.isShowWeb(data.channelLink ?: ""))
                startActivity(intent)
            }
        }
        addImageView = findViewById(R.id.add)
        addImageView.setOnClickListener {
            startActivity(Intent(this, ManagerActivity::class.java))
        }
        updateMessage = findViewById(R.id.update_message)
        updateMessage.setOnClickListener {
            //点击更新List
            updateMessage.visibility = View.GONE
            viewAdapter.notifyDataSetChanged()
        }
        refresh = findViewById(R.id.refresh)
        refresh.setOnClickListener {
            if(!swipeRefreshLayout.isRefreshing){
                swipeRefreshLayout.isRefreshing = true
                rssRepository.getRSSData()
            }
        }
        sort = findViewById(R.id.sort)
        sort.setOnClickListener {
            // 切换排序类型
            val sportsArray = arrayOf("全部", "未读", "已读")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("选择筛选类型")
                .setItems(
                    sportsArray,
                    DialogInterface.OnClickListener { dialog, which ->
                        RSSRepository.sortType = which
                        rssRepository.getRSSData()
                    })
            val dialog = builder.create()
            dialog.show()
        }
        notice = findViewById(R.id.notice)
        notice.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("嗨！")
                .setMessage("感谢你使用「想看」，这个APP目前还处于内测版，如果你有任何问题，都可以通过以下方式联系我：\n微博：ChenTianSaber\n邮箱：chentiansaber@qq.com")
                .setNegativeButton("好滴",DialogInterface.OnClickListener { dialog, which -> {} })
            val dialog = builder.create()
            dialog.show()
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
        }
    }

    private fun initData(){
        // 获取最新的时间
        sharedPreferences = getSharedPreferences("rss_info", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val latestPubDateStr = sharedPreferences.getString("latestPubDateMap", "{}")
        Log.d(TAG, "initData latestPubDateStr ---> $latestPubDateStr")
        parseStringToMap(latestPubDateStr!!)

        val latestTitleStr = sharedPreferences.getString("latestItemTitleMap", "{}")
        Log.d(TAG, "initData latestItemTitleMap ---> $latestTitleStr")
        parseStringToMap2(latestTitleStr!!)

        //获取已订阅的数据
        RSSInfoUtils.followRSSLink = sharedPreferences.getStringSet("followRSSLink", mutableSetOf()) as MutableSet<String>

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "xiangkan").build()
        rssRepository = RSSRepository(db.rssItemDao(), db.rssManagerInfoDao())
        rssViewModel = RSSViewModel(rssRepository)
        //观察数据
        rssViewModel.rssData.observe(this){
            val code = it.code
            val message = it.message
            val data = it.data
            // 更新
            Log.d(TAG, "onCreate: rssData.observe [code:$code message:$message]")

            if(code == RSSRepository.WEB_SUCCESS) swipeRefreshLayout.isRefreshing = false

            Log.d(TAG, "list ---> ${data.size} ${viewAdapter.dataList.size}")
            if(data.size > 0) emptyLayout.visibility = View.GONE
            if (data.size - viewAdapter.dataList.size > 0 || (RSSRepository.lastSortType != RSSRepository.sortType)) {
                if (viewAdapter.dataList.isEmpty() || (RSSRepository.lastSortType != RSSRepository.sortType)) {
                    //直接更新
                    RSSRepository.lastSortType = RSSRepository.sortType
                    updateMessage.visibility = View.GONE
                    viewAdapter.dataList = data
                    viewAdapter.notifyDataSetChanged()
                }else{
                    updateMessage.visibility = View.VISIBLE
                    updateMessage.text = "有${data.size - viewAdapter.dataList.size}条更新"
                    viewAdapter.dataList = data
                }
                // 这个时候lastestPubDateMap更新了，记下最新的时间
                Log.d(TAG, "rssViewModel.rssItemList.observe ---> ${RSSRepository.latestPubDateMap}  ${RSSRepository.latestItemTitleMap}")
                editor.putString("latestPubDateMap", RSSRepository.latestPubDateMap.toString())
                editor.putString("latestItemTitleMap", RSSRepository.latestItemTitleMap.toString())
                editor.putStringSet("followRSSLink", RSSInfoUtils.followRSSLink)
                editor.commit()
            }
        }
    }

    private fun parseStringToMap(mapString: String){
        if(mapString == "{}") return
        val string = mapString.substring(1, mapString.length - 1)
        val list = string.trim().split(",")
        for(str in list){
            val mapList = str.trim().split("=")
            RSSRepository.latestPubDateMap[mapList[0]] = mapList[1].toLong()
        }
        Log.d(TAG, "parseStringToMap ---> ${RSSRepository.latestPubDateMap}")
    }

    private fun parseStringToMap2(mapString: String){
        if(mapString == "{}") return
        val string = mapString.substring(1, mapString.length - 1)
        val list = string.trim().split(",")
        for(str in list){
            val mapList = str.trim().split("=")
            RSSRepository.latestItemTitleMap[mapList[0]] = mapList[1]
        }
        Log.d(TAG, "parseStringToMap ---> ${RSSRepository.latestItemTitleMap}")
    }

    override fun onDestroy() {
        MyEventBus.unregister(this)
        super.onDestroy()
    }

    interface ItemClick {
        fun onItemClick(itemView: View, data: RSSItem)
    }

    /**
     * 下拉刷新时回调
     */
    override fun onRefresh() {
        if(!swipeRefreshLayout.isRefreshing){
            Log.d(TAG, "onRefresh")
            swipeRefreshLayout.isRefreshing = true
            rssRepository.getRSSData()
        }
    }

    override fun addSuccess(rssManagerInfo: RSSManagerInfo) {
        rssRepository.addRSSManagerInfo(rssManagerInfo)
    }

}