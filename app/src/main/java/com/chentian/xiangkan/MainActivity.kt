package com.chentian.xiangkan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.blankj.utilcode.util.ToastUtils
import com.chentian.xiangkan.page.AddFragment
import com.chentian.xiangkan.page.DetailFragment
import com.chentian.xiangkan.page.HomeFragment
import com.chentian.xiangkan.page.ManagerFragment
import com.githang.statusbar.StatusBarCompat

class MainActivity : AppCompatActivity() ,View.OnClickListener{

    companion object{
        const val TAG = "MainActivity"
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
    private lateinit var updateTip:TextView

    private lateinit var rssRepository: RssRepository
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
        tabList = findViewById(R.id.tab_list)
        tabListAdapter = TabListAdapter()
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

        updateTip = findViewById(R.id.update_tip)
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
            rssRepository.getRssItemList()
        })
        // 监听内容数据的变化
        rssModel.rssItemsData.observe(this, Observer<ResponseData>{ response ->
            Log.d(TAG, "rssItemsData observe ---> $response")
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.sort -> {
                ToastUtils.showShort("排序选项")
            }
            R.id.manager -> {
                ToastUtils.showShort("管理订阅")
                val transient = supportFragmentManager.beginTransaction().apply{
                    replace(R.id.fragment_view,managerFragment)
                    addToBackStack("HomeFragment")
                }
                transient.commit()
            }
        }
    }

}