package com.chentian.xiangkan.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.chentian.xiangkan.*
import com.chentian.xiangkan.MainActivity
import com.chentian.xiangkan.adapter.TabListAdapter
import com.chentian.xiangkan.data.*
import com.chentian.xiangkan.dialog.SortDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 主页fragment
 * 所有的数据操作都需要通过MainActivity来操作，不允许直接接触rssRepository
 */
class HomeFragment : Fragment() {

    companion object{
        const val TAG = "HomeFragment"
    }

    // region field

    private lateinit var tabList: RecyclerView
    private lateinit var tabListAdapter: TabListAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var itemView: View
    private lateinit var sortBtn: ImageView
    private lateinit var syncBtn: ImageView

    private var fragmentList = mutableListOf<Fragment>()

    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.layout_home,container,false)
        initView()
        initData()
        return itemView
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun initView() {
        sortBtn = itemView.findViewById(R.id.sort)
        sortBtn.setOnClickListener {
            // 打开筛选面板，可以选择 全部，已读，未读
            activity?.let {
                SortDialog().show(it.supportFragmentManager,"sort")
            }
        }

        syncBtn = itemView.findViewById(R.id.sync)
        syncBtn.setOnClickListener {
            if(syncBtn.animation == null){
                startSyncAnimation()
            }else{
                stopSyncAnimation()
            }
        }

        tabList = itemView.findViewById(R.id.tab_list)
        tabListAdapter = TabListAdapter()
        tabListAdapter.setItemClick(activity as MainActivity)
        tabList.adapter = tabListAdapter
        tabList.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager = itemView.findViewById(R.id.view_pager)
        viewPager.adapter = pagerAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d(TAG, "onPageSelected: $position")
                val data = tabListAdapter.getDataList()[position]
                Toast.makeText(activity, data.channelTitle, Toast.LENGTH_SHORT).show()
                // 更新TAB选中状态
                changeTabChoosed(data)
                tabList.smoothScrollToPosition(position)
                tabListAdapter.notifyDataSetChanged()
            }
        })

    }

    private fun initData() {
        onRssLinkInfoDataChanged()
    }

    /**
     * 执行更新动画
     */
    private fun startSyncAnimation(){
        val animation = RotateAnimation(0f,360f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f)
        animation.repeatCount = -1
        animation.duration = 1200
        syncBtn.startAnimation(animation)
    }

    /**
     * 停止更新动画
     */
    private fun stopSyncAnimation(){
        syncBtn.animation = null
    }

    /**
     * 更新TAB的选中状态
     */
    private fun changeTabChoosed(rssLinkInfo: RssLinkInfo){
        for(data in tabListAdapter.getDataList()){
           data.isChoosed = false
        }
        rssLinkInfo.isChoosed = true
    }

    // region listen

    /**
     * 订阅源数据更新
     */
    private fun onRssLinkInfoDataChanged() {
        (activity as MainActivity).rssModel.rssLinksData.observe(this, Observer<ResponseData> { response ->
            Log.d(TAG, "rssLinksData observe ---> [$response]")

            // 过滤掉未订阅的数据源
            val dataList = mutableListOf<RssLinkInfo>()
            dataList.addAll(((response.data as MutableList<RssLinkInfo>).filter { it.state }).toMutableList())

            // 该数据为 "全部" TAB的占位数据
            dataList.add(0, RssLinkInfo(
                    url = RssLinkInfoFactory.ALLDATA,
                    channelLink = RssLinkInfoFactory.ALLDATA,
                    channelTitle = "全部",
                    channelDescription = "全部",
                    state = false,
                    icon = ""
            ))

            // 判断是否需要web请求,如果是的话那么 全部icon 变为loading状态
            when(response.code){
                ResponseCode.GET_RSSLINK_SUCCESS_NEED_REQUEST -> startSyncAnimation()
            }

            // 每次订阅源变动的时候，默认 全部 为选中状态
            changeTabChoosed(dataList[0])

            // 设置fragmentList
            fragmentList.clear()
            for(index in 0 until dataList.size){
                if(index == 0) {
                    fragmentList.add(AllContentListFragment(dataList[index]))
                }else{
                    fragmentList.add(ContentListFragment(dataList[index]))
                }
            }
            viewPager.adapter?.notifyDataSetChanged()

            tabListAdapter.setDataList(dataList)
            tabListAdapter.notifyDataSetChanged()
        })
    }

    /**
     * 内容数据更新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRssItemDataChanged(response: ResponseData) {

        val dataList = response.data as MutableList<RssItem>
        val code = response.code
        val message = response.message
        val tag = response.tag

        Log.d(TAG, "rssItemsData observe ---> $code dataList--> ${dataList.size} message --> $message tag --> $tag ")

        /**
         * 处理网络数据返回
         */
        fun handleWebResopnse(dataList: MutableList<RssItem>) {
            // 刷新标志取消
            stopSyncAnimation()
        }

        when (code) {
            ResponseCode.WEB_SUCCESS, ResponseCode.WEB_FAIL -> handleWebResopnse(dataList)
            ResponseCode.WEB_PROGRESS_SUCCESS -> Log.d(TAG, "请求完一个")
        }

    }

    // endregion

    private inner class ScreenSlidePagerAdapter(fa: Fragment) : FragmentStateAdapter(fa){
        override fun getItemCount(): Int {
            return fragmentList.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }

    }

}