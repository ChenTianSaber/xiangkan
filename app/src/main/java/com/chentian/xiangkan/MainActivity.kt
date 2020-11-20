package com.chentian.xiangkan

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.work.*
import com.chentian.xiangkan.data.*
import com.chentian.xiangkan.db.AppDatabase
import com.chentian.xiangkan.dialog.AddBiliBiliUpDialog
import com.chentian.xiangkan.dialog.SortDialog
import com.chentian.xiangkan.listener.ItemClickListener
import com.chentian.xiangkan.main.RssModel
import com.chentian.xiangkan.view.HomeFragment
import com.chentian.xiangkan.view.ManagerFragment
import com.chentian.xiangkan.repository.RssItemRepository
import com.chentian.xiangkan.repository.RssLinkRepository
import com.chentian.xiangkan.utils.AppUtils
import com.chentian.xiangkan.view.ContentActivity
import com.chentian.xiangkan.view.SettingFragment
import com.chentian.xiangkan.workmanager.UpdateDataWork
import com.githang.statusbar.StatusBarCompat
import java.util.concurrent.TimeUnit

/**
 * 页面的容器，这里会执行对数据的操作，其余的fragment只负责监听数据并更新
 * 相当于一个Presenter
 */
class MainActivity : AppCompatActivity(), View.OnClickListener, ItemClickListener {

    companion object {
        const val TAG = "MainActivity"
    }

    // region field

    private lateinit var homeFragment: HomeFragment
    private lateinit var managerFragment: ManagerFragment
    private lateinit var settingFragment: SettingFragment

    private lateinit var homeBtn: ImageView
    private lateinit var managerBtn: ImageView
    private lateinit var settingBtn: ImageView
    private lateinit var sortBtn: ImageView
    private lateinit var viewPager: ViewPager2

    lateinit var rssItemRepository: RssItemRepository
    lateinit var rssLinkRepository: RssLinkRepository
    lateinit var rssModel: RssModel

    private var fragmentList = mutableListOf<Fragment>()

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        StatusBarCompat.setStatusBarColor(this, resources.getColor(R.color.white_2), true)

        initView()
        initData()
    }

    override fun onPause() {
        // 记录上次阅读位置
        editor.putString("lastReadRssLink", RssItemData.tempLastReadRssLink)
        editor.commit()
        super.onPause()
    }

    private fun initView() {
        homeFragment = HomeFragment()
        managerFragment = ManagerFragment()
        settingFragment = SettingFragment()
        fragmentList.add(homeFragment)
        fragmentList.add(managerFragment)
        fragmentList.add(settingFragment)

        homeBtn = findViewById(R.id.home)
        homeBtn.setOnClickListener(this)
        managerBtn = findViewById(R.id.manager)
        managerBtn.setOnClickListener(this)
        settingBtn = findViewById(R.id.setting)
        settingBtn.setOnClickListener(this)
        sortBtn = findViewById(R.id.sort)
        sortBtn.setOnClickListener(this)

        viewPager = findViewById(R.id.view_pager)
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.isUserInputEnabled = false

    }

    private fun initData() {
        // 获取上次的最后一次阅读位置
        sharedPreferences = getSharedPreferences("rssData", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        RssItemData.lastReadRssLink = sharedPreferences.getString("lastReadRssLink","") ?: ""
        Log.d(TAG, "initData: RssItemData.lastReadRssLink --> ${RssItemData.lastReadRssLink}")

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "xiangkan").build()
        rssModel = RssModel()
        rssItemRepository = RssItemRepository(
            rssItemsData = rssModel.rssItemsData,
            rssItemDao = db.rssItemDao()
        )

        rssLinkRepository = RssLinkRepository(
            rssLinksData = rssModel.rssLinksData,
            rssLinkInfoDao = db.rssLinkInfoDao()
        )

        dataListen()

//        // TODO(TEST下面是测试代码)
//        rssLinkRepository.getAllRssLinkInfo(ResponseCode.GET_RSSLINK_SUCCESS_NEED_REQUEST)

        // TODO(在这里请求订阅源的数据)
        // 请求订阅源，请求完成后再请求内容数据
        rssLinkRepository.getAllRssLinkInfo(ResponseCode.GET_RSSLINK_SUCCESS_NO_REQUEST)

        // 创建WorkManager任务
//        val updateDataWorkRequest: PeriodicWorkRequest = PeriodicWorkRequestBuilder<UpdateDataWork>(8,TimeUnit.HOURS).build()
//        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
//            "updateRssItems",
//            ExistingPeriodicWorkPolicy.KEEP,
//            updateDataWorkRequest
//        )

    }

    /**
     * 监听数据源变动分发给对应的fragment
     */
    private fun dataListen() {

        /**
         * 处理订阅源变动事件
         */
        fun handleRssLinkInfoDataChanged(response: ResponseData) {
            val code = response.code
            val data = response.data as MutableList<RssLinkInfo>
            val message = response.message

            // 这里不管是请求DB还是web的数据，都只请求订阅源state为true的数据
            val dataList = (data.filter { it.state }).toMutableList()

            when(code){
                ResponseCode.GET_RSSLINK_SUCCESS_NEED_REQUEST -> {
                    // 请求web数据
                    rssItemRepository.getRssItems(dataList)
                }
                ResponseCode.GET_RSSLINK_SUCCESS_NEED_REQUEST_DB -> {
                    // 请求DB数据
                    rssItemRepository.getRssLinkInfoRssItems()
                }
            }

        }

        // 监听订阅源数据的变化
        rssModel.rssLinksData.observe(this, Observer<ResponseData> { response ->
            // Log.d(TAG, "rssLinksData observe ---> $response")
            handleRssLinkInfoDataChanged(response)
        })

        // 监听内容数据的变化
        rssModel.rssItemsData.observe(this, Observer<ResponseData> { response ->
            // val dataList = response.data as MutableList<RssItem>
            // Log.d(TAG, "rssItemsData observe ---> ${response.code} dataList-->${dataList.size} lastContentSize-->$lastContentSize")
        })
    }

    // region clickListener

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.home -> {
                viewPager.currentItem = 0
            }
            R.id.manager -> {
                viewPager.currentItem = 1
            }
            R.id.setting -> {
                viewPager.currentItem = 2
            }
            R.id.sort -> {
                // 打开筛选面板，可以选择 全部，已读，未读
                SortDialog().show(supportFragmentManager,"sort")
            }
        }
    }

    /**
     * 转发点击事件给对应的fragment，后续可改为注册模式，感兴趣的fragment即可监听
     */
    override fun onContentItemClick(itemView: View, data: RssItem) {
        Log.d(TAG, "onContentItemClick: $data")
        homeFragment.onContentItemClick(itemView, data)
        // TODO(将这个Item置为已读，并存入数据库)
        val intent = Intent(this, ContentActivity::class.java)
        intent.putExtra("RssItem", data)
        startActivity(intent)

        data.wasRead = true
        rssItemRepository.updateRssItem(data)
    }

    override fun onTabItemClick(itemView: View, data: RssLinkInfo) {
        Log.d(TAG, "onTabItemClick: $data")
        homeFragment.onTabItemClick(itemView, data)
    }

    override fun onManagerItemClick(itemView: View, data: RssLinkInfo) {
        Log.d(TAG, "onManagerItemClick: $data")
        // managerFragment会去进行它自己的处理，例如改变data的state，改变列表的UI
        managerFragment.onManagerItemClick(itemView, data)
        // 接下来是对数据的处理
        // 更新数据库, 更新首页TAB列表, 更新首页内容数据（只请求DB）
        rssLinkRepository.updateRssLinkInfo(data)
    }

    // endregion

    // region api

    /**
     * 获取这个订阅源中的所有数据，只获取DB数据，不请求web端
     * 首先将当前TAB数据切换，然后获取当前TAB下的数据
     */
    fun changeTabData(rssLinkInfo: RssLinkInfo) {
        if (rssLinkInfo.url == RssLinkInfoFactory.ALLDATA) {
            // 获取全部数据
            rssLinkRepository.getAllRssLinkInfo(ResponseCode.GET_RSSLINK_SUCCESS_NEED_REQUEST_DB)
        } else {
            // 获取对应的数据源的数据
            rssItemRepository.getSingleRssLinkInfoRssItems(rssLinkInfo)
        }
    }

    /**
     * 插入新的订阅源
     */
    fun insertRssLinkInfo(rssLinkInfo: RssLinkInfo){
        rssLinkRepository.insertRssLinkInfo(rssLinkInfo)
    }

    /**
     * 变更筛选条件
     */
    fun changeSortType(sortType: Int){
        RssItemRepository.SORT_TYPE = sortType
        rssLinkRepository.getAllRssLinkInfo(ResponseCode.GET_RSSLINK_SUCCESS_NEED_REQUEST_DB)
    }

    // endregion

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa){
        override fun getItemCount(): Int {
            return fragmentList.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }

    }

}