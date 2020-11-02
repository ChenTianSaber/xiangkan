package com.chentian.xiangkan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chentian.xiangkan.page.AddFragment
import com.chentian.xiangkan.page.DetailFragment
import com.chentian.xiangkan.page.HomeFragment
import com.chentian.xiangkan.page.ManagerFragment
import com.githang.statusbar.StatusBarCompat

class MainActivity : AppCompatActivity() ,View.OnClickListener, ItemClickListener, SwipeRefreshLayout.OnRefreshListener{

    companion object{
        const val TAG = "MainActivity"

        const val SORTANDMANAGER = 1
        const val BACK = 2
    }

    // region field

    private lateinit var tabList: RecyclerView
    private lateinit var tabListAdapter: TabListAdapter

    private lateinit var homeFragment: HomeFragment
    private lateinit var detailFragment: DetailFragment
    private lateinit var managerFragment:ManagerFragment
    private lateinit var addFragment: AddFragment

    private lateinit var sortBtn:ImageView
    private lateinit var managerBtn:ImageView
    private lateinit var backBtn:ImageView
    private lateinit var updateTip:TextView

    private lateinit var rssRepository: RssRepository
    lateinit var rssModel: RssModel

    var lastContentSize = 0

    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        StatusBarCompat.setStatusBarColor(this, resources.getColor(R.color.white_2), true)

        initView()
        initData()
    }

    private fun initView() {
        tabList = findViewById(R.id.tab_list)
        tabListAdapter = TabListAdapter()
        tabListAdapter.itemClick = this
        tabList.adapter = tabListAdapter
        tabList.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)

        homeFragment = HomeFragment()
        detailFragment = DetailFragment()
        managerFragment = ManagerFragment()
        addFragment = AddFragment()
        supportFragmentManager.beginTransaction().add(R.id.fragment_view,homeFragment).commit()

        sortBtn = findViewById(R.id.sort)
        sortBtn.setOnClickListener(this)
        managerBtn = findViewById(R.id.manager)
        managerBtn.setOnClickListener(this)
        backBtn = findViewById(R.id.back)
        backBtn.setOnClickListener(this)

        updateTip = findViewById(R.id.update_tip)
        updateTip.setOnClickListener(this)
    }

    private fun initData() {
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "xiangkan").build()

        rssModel = RssModel()
        dataListen()
        rssRepository = RssRepository(
                rssLinksData = rssModel.rssLinksData,
                rssItemsData = rssModel.rssItemsData,
                rssLinkInfoDao = db.rssLinkInfoDao(),
                rssItemDao = db.rssItemDao()
        )
        // 获取已订阅的链接
        rssRepository.getRssLinks()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        homeFragment.setItemClick(this)
    }

    private fun dataListen(){
        // 监听订阅源数据的变化
        rssModel.rssLinksData.observe(this, Observer<ResponseData>{ response ->
            Log.d(TAG, "rssLinksData observe ---> $response")
            tabListAdapter.dataList = response.data as MutableList<RssLinkInfo>
            // 设置"全部"TAB
            tabListAdapter.dataList.add(0, RssLinkInfo(
                    url = "-1",
                    channelLink = "-1",
                    channelTitle = "全部",
                    channelDescription = "全部内容",
                    state = false
            ))
            tabListAdapter.notifyDataSetChanged()
            // 获取内容列表
            rssRepository.getRssItemList(tabListAdapter.dataList[0],true)
        })
        // 监听内容数据的变化
        rssModel.rssItemsData.observe(this, Observer<ResponseData>{ response ->
            val dataList = response.data as MutableList<RssItem>
            Log.d(TAG, "rssItemsData observe ---> ${response.code} dataList-->${dataList.size} lastContentSize-->$lastContentSize")
            if(response.code == RssRepository.WEB_SUCCESS){
                homeFragment.setIsRefreshing(false)
                updateTip.text = "有${dataList.size - lastContentSize}条更新"
                updateTip.visibility = if(dataList.size - lastContentSize > 0) View.VISIBLE else View.GONE
                if(lastContentSize <= 0){
                    homeFragment.refreshData()
                }
                if((dataList.size - lastContentSize) <= 0){
                    Toast.makeText(this,"没有内容更新",Toast.LENGTH_SHORT).show()
                }
                lastContentSize = dataList.size
            }else if(response.code == RssRepository.DB_SUCCESS){
                lastContentSize = dataList.size
                homeFragment.refreshData()
            }
        })
    }

    // 在不同的页面中，需要显示不同的按钮
    private fun changeIcon(type:Int){
        when(type){
            SORTANDMANAGER -> {// 展示排序和管理按钮
                managerBtn.visibility = View.VISIBLE
                sortBtn.visibility = View.VISIBLE
                backBtn.visibility = View.GONE
                tabList.visibility = View.VISIBLE
            }
            BACK -> {// 展示返回按钮
                managerBtn.visibility = View.GONE
                sortBtn.visibility = View.GONE
                backBtn.visibility = View.VISIBLE
                tabList.visibility = View.GONE
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.sort -> {

            }
            R.id.manager -> {
                val transient = supportFragmentManager.beginTransaction().apply{
                    replace(R.id.fragment_view,managerFragment)
                    addToBackStack("HomeFragment")
                }
                transient.commit()
                changeIcon(BACK)
            }
            R.id.back -> {
                supportFragmentManager.popBackStack()
                changeIcon(SORTANDMANAGER)
            }
            R.id.update_tip -> {
                // 点击之后更新列表并返回顶部
                homeFragment.refreshData()
                homeFragment.scrollToTop()
                updateTip.visibility = View.GONE
            }
        }
    }

    override fun onContentItemClick(itemView: View, data: RssItem) {
        val transient = supportFragmentManager.beginTransaction().apply{
            replace(R.id.fragment_view,detailFragment)
            addToBackStack("HomeFragment")
        }
        val bundle = Bundle()
        bundle.putParcelable("RssItem",data)
        detailFragment.arguments = bundle
        transient.commit()
        changeIcon(BACK)
    }

    override fun onTabItemClick(itemView: View, data: RssLinkInfo) {
        Log.d(TAG, "onTabItemClick: $data")
        // 获取内容列表
        rssRepository.getRssItemList(data,false)
    }

    override fun onRefresh() {
        Log.d(TAG, "onRefresh ${homeFragment.isRefreshing()}")
        // 获取内容列表
        rssRepository.getRssItemList(tabListAdapter.dataList[0], true)
    }

}