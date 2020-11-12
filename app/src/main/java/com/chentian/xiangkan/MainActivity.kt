package com.chentian.xiangkan

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.room.Room
import com.chentian.xiangkan.data.ResponseCode
import com.chentian.xiangkan.data.ResponseData
import com.chentian.xiangkan.data.RssItem
import com.chentian.xiangkan.data.RssLinkInfo
import com.chentian.xiangkan.db.AppDatabase
import com.chentian.xiangkan.listener.ItemClickListener
import com.chentian.xiangkan.main.RssModel
import com.chentian.xiangkan.view.ContentFragment
import com.chentian.xiangkan.view.HomeFragment
import com.chentian.xiangkan.view.ManagerFragment
import com.chentian.xiangkan.repository.RssItemRepository
import com.chentian.xiangkan.repository.RssLinkRepository
import com.chentian.xiangkan.utils.AppUtils
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
    private lateinit var contentFragment: ContentFragment
    private lateinit var managerFragment: ManagerFragment

    private lateinit var homeBtn: ImageView
    private lateinit var managerBtn: ImageView

    lateinit var rssItemRepository: RssItemRepository
    lateinit var rssLinkRepository: RssLinkRepository
    lateinit var rssModel: RssModel

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
        homeFragment = HomeFragment()
        contentFragment = ContentFragment()
        managerFragment = ManagerFragment()
        supportFragmentManager.beginTransaction().add(R.id.fragment_view, homeFragment).commit()

        homeBtn = findViewById(R.id.home)
        homeBtn.setOnClickListener(this)
        managerBtn = findViewById(R.id.manager)
        managerBtn.setOnClickListener(this)
    }

    private fun initData() {
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

        // TODO(在这里请求订阅源的数据)
        // 请求订阅源，请求完成后再请求内容数据
        rssLinkRepository.getAllRssLinkInfo(ResponseCode.GET_RSSLINK_SUCCESS_NEED_REQUEST)

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

            when(code){
                ResponseCode.GET_RSSLINK_SUCCESS_NEED_REQUEST -> {
                    // 请求内容数据
                    rssItemRepository.getRssItems(data)
                }
            }

        }

        // 监听订阅源数据的变化
        rssModel.rssLinksData.observe(this, Observer<ResponseData> { response ->
            // Log.d(TAG, "rssLinksData observe ---> $response")
            homeFragment.onRssLinkInfoDataChanged(response)
            managerFragment.onRssLinkInfoDataChanged(response)
            handleRssLinkInfoDataChanged(response)
        })

        // 监听内容数据的变化
        rssModel.rssItemsData.observe(this, Observer<ResponseData> { response ->
            // val dataList = response.data as MutableList<RssItem>
            // Log.d(TAG, "rssItemsData observe ---> ${response.code} dataList-->${dataList.size} lastContentSize-->$lastContentSize")
            homeFragment.onRssItemDataChanged(response)
        })
    }

    // region clickListener

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.home -> {
                AppUtils.navigateFragment(this, homeFragment)
            }
            R.id.manager -> {
                AppUtils.navigateFragment(this, managerFragment)
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
    }

    override fun onTabItemClick(itemView: View, data: RssLinkInfo) {
        Log.d(TAG, "onTabItemClick: $data")
        homeFragment.onTabItemClick(itemView, data)
    }

    override fun onManagerItemClick(itemView: View, data: RssLinkInfo) {
        Log.d(TAG, "onManagerItemClick: $data")
        managerFragment.onManagerItemClick(itemView, data)
    }

    // endregion

    // region api

    /**
     * 获取这个订阅源中的所有数据，只获取DB数据，不请求web端
     * 首先将当前TAB数据切换，然后获取当前TAB下的数据
     */
    fun changeTabData(rssLinkInfo: RssLinkInfo){
        rssItemRepository.getSingleRssLinkInfoRssItems(rssLinkInfo)
    }

    /**
     * 获取订阅源，获取之后不会请求web数据
     */
    fun getRssLinks(){
        rssLinkRepository.getAllRssLinkInfo(ResponseCode.GET_RSSLINK_SUCCESS_NO_REQUEST)
    }

    // endregion

}