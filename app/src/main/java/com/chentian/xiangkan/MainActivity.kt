package com.chentian.xiangkan

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.chentian.xiangkan.data.*
import com.chentian.xiangkan.db.AppDatabase
import com.chentian.xiangkan.listener.ItemClickListener
import com.chentian.xiangkan.main.RssModel
import com.chentian.xiangkan.view.HomeFragment
import com.chentian.xiangkan.view.ManagerFragment
import com.chentian.xiangkan.repository.RssItemRepository
import com.chentian.xiangkan.repository.RssLinkRepository
import com.chentian.xiangkan.utils.RssUtils
import com.chentian.xiangkan.view.content.ContentActivity
import com.chentian.xiangkan.view.SettingFragment
import com.chentian.xiangkan.view.content.ContentWebVideoActivity
import com.githang.statusbar.StatusBarCompat

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

    private lateinit var homeBtn: LinearLayout
    private lateinit var managerBtn: LinearLayout
    private lateinit var settingBtn: LinearLayout
    private lateinit var homeImage: ImageView
    private lateinit var managerImage: ImageView
    private lateinit var settingImage: ImageView
    private lateinit var homeText: TextView
    private lateinit var managerText: TextView
    private lateinit var settingText: TextView

    private lateinit var viewPager: ViewPager2

    lateinit var rssItemRepository: RssItemRepository
    lateinit var rssLinkRepository: RssLinkRepository
    lateinit var rssModel: RssModel

    private var fragmentList = mutableListOf<Fragment>()

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private var needRequestRssData: Boolean = false

    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        StatusBarCompat.setStatusBarColor(this, resources.getColor(R.color.white), true)

        initView()
        initData()
    }

    override fun onPause() {
        // 记录上次阅读位置
        editor.putString("lastReadRssLink", RssItemData.tempLastReadRssLink)
        editor.putString("rssLinkLastRequest", RssItemRepository.rssLinkLastRequest.toString())
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
        homeImage = findViewById(R.id.home_image)
        homeText = findViewById(R.id.home_text)
        managerBtn = findViewById(R.id.manager)
        managerBtn.setOnClickListener(this)
        managerImage = findViewById(R.id.manager_image)
        managerText = findViewById(R.id.manager_text)
        settingBtn = findViewById(R.id.setting)
        settingBtn.setOnClickListener(this)
        settingImage = findViewById(R.id.setting_image)
        settingText = findViewById(R.id.setting_text)

        viewPager = findViewById(R.id.view_pager)
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.isUserInputEnabled = false

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                if(position == 0){
                    // 当回到首页的时候，需要判断一下是否需要刷新数据，要的话那就去请求数据
                    if(needRequestRssData){
                        rssLinkRepository.getAllRssLinkInfo(ResponseCode.GET_RSSLINK_SUCCESS_NEED_REQUEST)
                        needRequestRssData = false
                    }
                }
            }
        })

        changeBottomTab(0)

    }

    private fun initData() {
        sharedPreferences = getSharedPreferences("rssData", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // 获取上次的最后一次阅读位置
        RssItemData.lastReadRssLink = sharedPreferences.getString("lastReadRssLink","") ?: ""
        Log.d(TAG, "initData: RssItemData.lastReadRssLink --> [${RssItemData.lastReadRssLink}]")

        fun parseStringToMap(mapString: String){
            if(mapString == "{}") return
            val string = mapString.substring(1, mapString.length - 1)
            val list = string.trim().split(",")
            for(str in list){
                val mapList = str.trim().split("=")
                RssItemRepository.rssLinkLastRequest[mapList[0]] = mapList[1].toLong()
            }
            Log.d(TAG, "parseStringToMap ---> [${RssItemRepository.rssLinkLastRequest}]")
        }

        // 获取上次请求的配置
        val str = sharedPreferences.getString("rssLinkLastRequest","")
        if (!str.isNullOrEmpty()) {
            parseStringToMap(str)
        }

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "xiangkan").build()
        rssModel = RssModel()
        rssItemRepository = RssItemRepository(
            rssItemDao = db.rssItemDao()
        )

        rssLinkRepository = RssLinkRepository(
            rssLinksData = rssModel.rssLinksData,
            rssLinkInfoDao = db.rssLinkInfoDao()
        )

        dataListen()

        // 请求订阅源数据
        rssLinkRepository.getAllRssLinkInfo(ResponseCode.GET_RSSLINK_SUCCESS_NEED_REQUEST)

    }

    /**
     * 监听数据源变动
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
    }

    // region clickListener

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.home -> {
                changeBottomTab(0)
            }
            R.id.manager -> {
                changeBottomTab(1)
            }
            R.id.setting -> {
                changeBottomTab(2)
            }
        }
    }

    private fun changeBottomTab(position: Int){

        viewPager.setCurrentItem(position,true)

        homeImage.setImageResource(R.mipmap.bell_fill_gray)
        managerImage.setImageResource(R.mipmap.book_gray)
        settingImage.setImageResource(R.mipmap.bulb_gray)

        homeText.visibility = View.GONE
        managerText.visibility = View.GONE
        settingText.visibility = View.GONE

        when(position){
            0 -> {
                homeImage.setImageResource(R.mipmap.bell_fill_black)
                homeText.visibility = View.VISIBLE
            }
            1 -> {
                managerImage.setImageResource(R.mipmap.book_black)
                managerText.visibility = View.VISIBLE
            }
            2 -> {
                settingImage.setImageResource(R.mipmap.bulb_black)
                settingText.visibility = View.VISIBLE
            }
        }
    }

    /**
     * 转发点击事件给对应的fragment，后续可改为注册模式，感兴趣的fragment即可监听
     */
    override fun onContentItemClick(itemView: View, data: RssItem) {
        Log.d(TAG, "onContentItemClick: [$data]")

        val intent = when(RssUtils.getViewTypeByChannelLink(data.channelLink)){
            RssUtils.VIEW_TYPE_VIDEO -> Intent(this, ContentWebVideoActivity::class.java)
            else -> Intent(this, ContentActivity::class.java)
        }
        intent.putExtra("RssItem", data)
        startActivity(intent)

        // 将这个Item置为已读，并存入数据库
        data.wasRead = true
        rssItemRepository.updateRssItem(data)
    }

    override fun onMarkReadClick(itemView: View, data: RssItem) {
        if(data.wasRead){
            return
        }
        data.wasRead = true
        rssItemRepository.updateRssItem(data)
    }

    override fun onTabItemClick(itemView: View, data: RssLinkInfo) {
        Log.d(TAG, "onTabItemClick: $data")
    }

    override fun onManagerItemClick(itemView: View, data: RssLinkInfo) {
        Log.d(TAG, "onManagerItemClick: $data")
        // managerFragment会去进行它自己的处理，例如改变data的state，改变列表的UI
        managerFragment.onManagerItemClick(itemView, data)
        // 接下来是对数据的处理
        // 更新数据库, 更新首页TAB列表, 更新首页内容数据（只请求DB）
        rssLinkRepository.updateRssLinkInfo(data)

        // 当把订阅源从未订阅变成已订阅之后，就需要请求
        if(data.state){
            needRequestRssData = true
        }
    }

    // endregion

    // region api

    /**
     * 获取这个TAB下的数据
     * @param rssLinkInfo
     */
    fun getTabData(rssLinkInfo: RssLinkInfo) {
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