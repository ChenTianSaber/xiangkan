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

/**
 * 主页列表单个订阅源内容的fragment
 * 所有的数据操作都需要通过MainActivity来操作，不允许直接接触rssRepository
 */
class ContentListFragment(var rssLinkInfo: RssLinkInfo) : Fragment() {

    companion object{
        const val TAG = "ContentListFragment"
    }

    // region field

    private lateinit var contentList: RecyclerView
    private lateinit var contentListAdapter: ContentListAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

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
        contentListAdapter.setContentListType(ResponseCode.SINGLE)
        contentList.adapter = contentListAdapter
        contentList.layoutManager = LinearLayoutManager(activity)
        contentListAdapter.setItemClick(activity as MainActivity)

        swipeRefreshLayout = itemView.findViewById(R.id.swipe_refresh)
        swipeRefreshLayout.isEnabled = false

        emptyLayout = itemView.findViewById(R.id.empty_layout)
    }

    private fun initData() {
        Log.d(TAG, "initData: $rssLinkInfo")
        onRssItemDataChanged()
        (activity as MainActivity).changeTabData(rssLinkInfo)
    }

    private fun refreshData(){
        contentListAdapter.notifyDataSetChanged()
    }

    // region listen

    /**
     * 内容数据更新
     */
    private fun onRssItemDataChanged() {
        // 监听内容数据的变化
        (activity as MainActivity).rssModel.rssItemsData.observe(this, Observer<ResponseData> { response ->
            // Log.d(TAG, "rssItemsData observe ---> $response")
            val dataList = response.data as MutableList<RssItem>
            val code = response.code
            val message = response.message
            val tag = response.tag

            Log.d(TAG, "[${rssLinkInfo.channelTitle}] rssItemsData observe ---> $code dataList--> ${dataList.size} message --> $message tag --> $tag")

            /**
             * 处理DB数据返回
             */
            fun handleDBResopnse(dataList: MutableList<RssItem>){
                // TODO(直接展示, 更新lastContentSize)
                // 先判断是不是这个TAB下的数据
                if(tag == ResponseCode.SINGLE && (dataList.isNotEmpty() && dataList[0].channelLink == rssLinkInfo.channelLink)){
                    contentListAdapter.setDataList(dataList)
                    refreshData()
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
                ResponseCode.DB_SUCCESS -> handleDBResopnse(dataList)
                ResponseCode.UPDATE_RSSITEM -> {
                    refreshData()
                }
            }

            checkIsListEmpty()
        })
    }

    // endregion

}