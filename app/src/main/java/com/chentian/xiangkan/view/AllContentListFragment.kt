package com.chentian.xiangkan.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chentian.xiangkan.*
import com.chentian.xiangkan.MainActivity
import com.chentian.xiangkan.adapter.TabListAdapter
import com.chentian.xiangkan.adapter.ContentListAdapter
import com.chentian.xiangkan.data.*
import com.chentian.xiangkan.repository.RssLinkRepository
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 主页列表全部内容的fragment
 * 所有的数据操作都需要通过MainActivity来操作，不允许直接接触rssRepository
 */
class AllContentListFragment(var rssLinkInfo: RssLinkInfo) : Fragment() ,SwipeRefreshLayout.OnRefreshListener{

    companion object{
        const val TAG = "AllContentListFragment"
    }

    // region field

    private lateinit var contentList: RecyclerView
    private lateinit var contentListAdapter: ContentListAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var emptyLayout: LinearLayout
    private lateinit var updateTip: TextView
    private lateinit var itemView: View

    private var lastContentSize = 0
    private var lastWebContentData: MutableList<RssItem>? = null
    private var hasScrolled: Boolean = false

    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.layout_contentlist,container,false)
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
        contentList = itemView.findViewById(R.id.content_list)
        contentListAdapter = ContentListAdapter()
        contentListAdapter.setContentListType(ResponseCode.ALL)
        contentList.adapter = contentListAdapter
        contentList.layoutManager = LinearLayoutManager(activity)
        contentListAdapter.setItemClick(activity as MainActivity)

        swipeRefreshLayout = itemView.findViewById(R.id.swipe_refresh)
        swipeRefreshLayout.setOnRefreshListener(this)
        swipeRefreshLayout.isEnabled = false

        emptyLayout = itemView.findViewById(R.id.empty_layout)
        updateTip = itemView.findViewById(R.id.update_tip)
        updateTip.setOnClickListener {
            // TODO(点击之后刷新数据，列表回到顶部，然后把自己隐藏)
            lastWebContentData?.let {
                contentListAdapter.setDataList(it)
                refreshData()
                lastWebContentData = null
                updateTip.visibility = View.GONE
                emptyLayout.visibility = View.GONE
            }
        }

        contentList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                //滑动结束
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 记录第一个可见的item，这就是上次阅读的位置
                    val position = (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                    if(position >= 0){
                        RssItemData.tempLastReadRssLink = contentListAdapter.getDataList()[position].link
                        Log.d(TAG, "onScrollStateChanged: RssItemData.tempLastReadRssLink --> ${RssItemData.tempLastReadRssLink}")
                    }
                }
            }
        })
    }

    private fun initData() {
        Log.d(TAG, "initData: $rssLinkInfo")
//        onRssItemDataChanged()
        (activity as MainActivity).changeTabData(rssLinkInfo)
    }

    private fun refreshData(){
        contentListAdapter.notifyDataSetChanged()
        if(!hasScrolled){
            for(index in 0 until contentListAdapter.getDataList().size){
                val data = contentListAdapter.getDataList()[index]
                if(data.link == RssItemData.lastReadRssLink){
                    contentList.smoothScrollToPosition(index)
                    hasScrolled = true
                    return
                }
            }
        }
    }

    // region listen

    /**
     * 内容数据更新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRssItemDataChanged(response: ResponseData) {
        // 监听内容数据的变化
        // Log.d(TAG, "rssItemsData observe ---> $response")
        val dataList = response.data as MutableList<RssItem>
        val code = response.code
        val message = response.message
        val tag = response.tag

        Log.d(TAG, "rssItemsData observe ---> $code dataList--> ${dataList.size} message --> $message tag --> $tag lastContentSize--> $lastContentSize ")

        /**
         * 处理网络数据返回
         */
        fun handleWebResopnse(dataList: MutableList<RssItem>) {
            // TODO(刷新标志取消，判断数据有无更新，有的话弹出更新tip，等点击tip再刷新列表，更新lastContentSize)

            when (code) {
                ResponseCode.WEB_SUCCESS -> {
                    Toast.makeText(activity, "更新数据成功~", Toast.LENGTH_SHORT).show()
                    val updateSum = dataList.size - lastContentSize
                    if(updateSum > 0){
                        updateTip.text = "有 $updateSum 条更新"
                        updateTip.visibility = View.VISIBLE
                        lastWebContentData = dataList
                    }
                    lastContentSize = dataList.size
                }
                ResponseCode.WEB_FAIL -> {
                    Toast.makeText(activity, "获取订阅数据失败", Toast.LENGTH_SHORT).show()
                }
            }
        }

        /**
         * 处理DB数据返回
         */
        fun handleDBResopnse(dataList: MutableList<RssItem>){
            // TODO(直接展示, 更新lastContentSize)
            // 先判断是不是这个TAB下的数据
            if(tag == ResponseCode.ALL){
                contentListAdapter.setDataList(dataList)
                refreshData()

                lastContentSize = dataList.size
            }
        }

        /**
         * 检查列表是否是空状态
         */
        fun checkIsListEmpty(){
            if (contentListAdapter.isListEmpty()) {
                emptyLayout.visibility = View.VISIBLE
            } else {
                emptyLayout.visibility = View.GONE
            }
        }

        when (code) {
            ResponseCode.WEB_SUCCESS -> handleWebResopnse(dataList)
            ResponseCode.WEB_FAIL -> handleWebResopnse(dataList)
            ResponseCode.DB_SUCCESS -> handleDBResopnse(dataList)
            ResponseCode.UPDATE_RSSITEM -> {
                refreshData()
            }
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