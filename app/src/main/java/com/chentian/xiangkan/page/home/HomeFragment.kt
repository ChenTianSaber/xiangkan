package com.chentian.xiangkan.page.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import com.chentian.xiangkan.repository.RssRepository

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
    private lateinit var updateTip: TextView
    private lateinit var itemView: View

    var lastContentSize = 0

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
        tabList.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)

        swipeRefreshLayout = itemView.findViewById(R.id.swipe_refresh)
        swipeRefreshLayout.setOnRefreshListener(this)
        swipeRefreshLayout.isEnabled = false

        emptyLayout = itemView.findViewById(R.id.empty_layout)
        updateTip = itemView.findViewById(R.id.update_tip)
        updateTip.setOnClickListener {
            // TODO(点击之后刷新数据，列表回到顶部，然后把自己隐藏)
        }
    }

    private fun initData() {

    }

    private fun refreshData(){
        contentListAdapter.notifyDataSetChanged()
    }

    // region listen

    /**
     * 点击内容Item回调
     */
    fun onContentItemClick(itemView: View, data: RssItem){
        // TODO(跳转到内容fragment)
    }

    /**
     * 点击TAB切换回调
     */
    fun onTabItemClick(itemView: View, data: RssLinkInfo){
        // TODO(更新当前选中TAB，请求对应的TAB数据)
    }

    /**
     * 订阅源数据更新
     */
    fun onRssLinkInfoDataChanged(response: ResponseData) {
        Log.d(MainActivity.TAG, "rssLinksData observe ---> $response")
        // 过滤掉未订阅的数据源
        tabListAdapter.dataList = ((response.data as MutableList<RssLinkInfo>).filter { it.state }).toMutableList()
        tabListAdapter.notifyDataSetChanged()
        // TODO(更新内容列表)
    }

    /**
     * 内容数据更新
     */
    fun onRssItemDataChanged(response: ResponseData) {
        // Log.d(TAG, "rssItemsData observe ---> $response")
        val dataList = response.data as MutableList<RssItem>
        val code = response.code
        val message = response.message

        Log.d(TAG, "rssItemsData observe ---> $code dataList--> ${dataList.size} message --> $message lastContentSize--> $lastContentSize")

        /**
         * 处理网络数据返回
         */
        fun handleWebResopnse(dataList: MutableList<RssItem>){
            // TODO(下拉刷新标志取消，判断数据有无更新，有的话弹出更新tip，等点击tip再刷新列表，更新lastContentSize)
        }

        /**
         * 处理DB数据返回
         */
        fun handleDBResopnse(dataList: MutableList<RssItem>){
            // TODO(直接展示, 更新lastContentSize)
        }

        /**
         * 检查列表是否是空状态
         */
        fun checkIsListEmpty(){
            if (contentListAdapter.dataList.isNullOrEmpty()) {
                emptyLayout.visibility = View.VISIBLE
            } else {
                emptyLayout.visibility = View.GONE
            }
        }

        when(code){
            RssRepository.WEB_SUCCESS -> handleWebResopnse(dataList)
            RssRepository.DB_SUCCESS -> handleDBResopnse(dataList)
        }

        checkIsListEmpty()
    }

    /**
     * 下拉刷新回调
     */
    override fun onRefresh() {
        Log.d(MainActivity.TAG, "onRefresh ${swipeRefreshLayout.isRefreshing}")
        // TODO(这个时候需要去获取新的数据)
    }

    // endregion

}