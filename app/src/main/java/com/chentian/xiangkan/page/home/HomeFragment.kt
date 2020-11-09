package com.chentian.xiangkan.page.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chentian.xiangkan.*
import com.chentian.xiangkan.data.ResponseData
import com.chentian.xiangkan.data.RssItem
import com.chentian.xiangkan.data.RssLinkInfo
import com.chentian.xiangkan.main.MainActivity
import com.chentian.xiangkan.main.TabListAdapter
import com.chentian.xiangkan.page.detail.ContentListAdapter

/**
 * 主页
 */
class HomeFragment : Fragment() ,SwipeRefreshLayout.OnRefreshListener{

    companion object{
        const val TAG = "HomeFragment"
    }

    // region field

    private lateinit var contentList: RecyclerView
    private lateinit var contentListAdapter: ContentListAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var tabList: RecyclerView
    private lateinit var tabListAdapter: TabListAdapter

    private lateinit var emptyLayout: LinearLayout

    private lateinit var itemView: View
    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.layout_contentlist,container,false)
        initView()
        initData()
        return itemView
    }

    private fun initView() {
        contentList = itemView.findViewById(R.id.content_list)
        contentListAdapter = ContentListAdapter()
        contentList.adapter = contentListAdapter
        contentList.layoutManager = LinearLayoutManager(activity)
        contentListAdapter.itemClick = activity as MainActivity

        tabList = itemView.findViewById(R.id.tab_list)
        tabListAdapter = TabListAdapter()
        tabListAdapter.itemClick = activity as MainActivity
        tabList.adapter = tabListAdapter
        tabList.layoutManager = LinearLayoutManager(activity,RecyclerView.HORIZONTAL,false)

        swipeRefreshLayout = itemView.findViewById(R.id.swipe_refresh)
        swipeRefreshLayout.setOnRefreshListener(this)
        swipeRefreshLayout.isEnabled = false

        emptyLayout = itemView.findViewById(R.id.empty_layout)
    }

    fun isRefreshing(): Boolean {
        return swipeRefreshLayout.isRefreshing
    }

    fun setIsRefreshing(value: Boolean) {
        swipeRefreshLayout.isRefreshing = value
    }

    fun refreshData(){
        contentListAdapter.notifyDataSetChanged()
    }

    fun scrollToTop(){
        contentList.smoothScrollToPosition(0)
    }

    override fun onRefresh() {
        Log.d(MainActivity.TAG, "onRefresh ${isRefreshing()}")
        // 获取内容列表
        (activity as MainActivity).rssRepository.getRssItemList(tabListAdapter.dataList[0], true)
    }

    private fun initData() {
        // 监听订阅源数据的变化
        (activity as MainActivity).rssModel.rssLinksData.observe(this, Observer<ResponseData>{ response ->
            Log.d(MainActivity.TAG, "rssLinksData observe ---> $response")
            tabListAdapter.dataList =
                ((response.data as MutableList<RssLinkInfo>).filter { it.state }).toMutableList()
            // 设置"全部"TAB
            if(tabListAdapter.dataList.isEmpty() || tabListAdapter.dataList[0].url != "-1"){
                tabListAdapter.dataList.add(0, RssLinkInfo(
                    url = "-1",
                    channelLink = "-1",
                    channelTitle = "全部",
                    channelDescription = "全部内容",
                    state = false
                ))
            }
            tabListAdapter.notifyDataSetChanged()
            // 获取内容列表
            (activity as MainActivity).rssRepository.getRssItemList(tabListAdapter.dataList[0],true)
        })

        // 监听内容数据的变化
        (activity as MainActivity).rssModel.rssItemsData.observe(this, Observer<ResponseData>{ response ->
//            Log.d(TAG, "rssItemsData observe ---> $response")
            contentListAdapter.dataList = response.data as MutableList<RssItem>
            if(contentListAdapter.dataList.isEmpty()){
                emptyLayout.visibility = View.VISIBLE
            }else{
                emptyLayout.visibility = View.GONE
            }
//            contentListAdapter.notifyDataSetChanged()
        })
    }

}