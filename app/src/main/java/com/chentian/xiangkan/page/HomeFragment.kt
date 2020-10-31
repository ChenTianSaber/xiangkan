package com.chentian.xiangkan.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chentian.xiangkan.*

/**
 * 主页
 */
class HomeFragment : Fragment() {

    companion object{
        const val TAG = "HomeFragment"
    }

    // region field

    private lateinit var contentList: RecyclerView
    private lateinit var contentListAdapter: ContentListAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

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
    }

    private fun initData() {
        // 监听内容数据的变化
        (activity as MainActivity).rssModel.rssItemsData.observe(this, Observer<ResponseData>{ response ->
            Log.d(TAG, "rssItemsData observe ---> $response")
            contentListAdapter.dataList = response.data as MutableList<RssItem>
            contentListAdapter.notifyDataSetChanged()
        })
    }

}