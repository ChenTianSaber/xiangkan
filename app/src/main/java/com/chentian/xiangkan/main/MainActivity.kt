package com.chentian.xiangkan.main

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chentian.xiangkan.*
import com.chentian.xiangkan.data.ResponseData
import com.chentian.xiangkan.data.RssItem
import com.chentian.xiangkan.data.RssLinkInfo
import com.chentian.xiangkan.db.AppDatabase
import com.chentian.xiangkan.listener.ItemClickListener
import com.chentian.xiangkan.page.detail.ContentFragment
import com.chentian.xiangkan.page.home.HomeFragment
import com.chentian.xiangkan.page.manager.ManagerFragment
import com.githang.statusbar.StatusBarCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() ,View.OnClickListener, ItemClickListener{

    companion object{
        const val TAG = "MainActivity"

        const val SORTANDMANAGER = 1
        const val BACK = 2
    }

    // region field

    private lateinit var homeFragment: HomeFragment
    private lateinit var contentFragment: ContentFragment
    private lateinit var managerFragment: ManagerFragment

    private lateinit var homeBtn:ImageView
    private lateinit var managerBtn:ImageView

    private lateinit var updateTip:TextView

    lateinit var rssRepository: RssRepository
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

        homeFragment = HomeFragment()
        contentFragment = ContentFragment()
        managerFragment = ManagerFragment()
        supportFragmentManager.beginTransaction().add(R.id.fragment_view,homeFragment).commit()

        homeBtn = findViewById(R.id.home)
        homeBtn.setOnClickListener(this)
        managerBtn = findViewById(R.id.manager)
        managerBtn.setOnClickListener(this)

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

    private fun dataListen(){
        // 监听订阅源数据的变化
        rssModel.rssLinksData.observe(this, Observer<ResponseData>{ response ->
//            Log.d(TAG, "rssLinksData observe ---> $response")
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
                    updateTip.visibility = View.GONE
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

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.home -> {
                val transient = supportFragmentManager.beginTransaction().apply{
                    replace(R.id.fragment_view,homeFragment)
                }
                transient.commit()
            }
            R.id.manager -> {
                val transient = supportFragmentManager.beginTransaction().apply{
                    replace(R.id.fragment_view,managerFragment)
                }
                transient.commit()
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
            replace(R.id.fragment_view,contentFragment)
            addToBackStack("HomeFragment")
        }
        val bundle = Bundle()
        bundle.putParcelable("RssItem",data)
        contentFragment.arguments = bundle
        transient.commit()

        data.wasRead = true
        GlobalScope.launch (Dispatchers.IO){
            rssRepository.rssItemDao.updateItems(data)
        }
        homeFragment.refreshData()
    }

    override fun onTabItemClick(itemView: View, data: RssLinkInfo) {
        Log.d(TAG, "onTabItemClick: $data")
        // 获取内容列表
        rssRepository.getRssItemList(data,false)
    }

    override fun onManagerItemClick(itemView: View, data: RssLinkInfo) {
        // 点击订阅管理的Item
        if(data.url == "-1"){
            Toast.makeText(this,"~",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this,"${data.channelTitle} state --> ${data.state} ",Toast.LENGTH_SHORT).show()
            data.state = !data.state
            rssRepository.updateRssLink(data)
            rssRepository.getRssLinks()
        }
    }

}