package com.chentian.xiangkan.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chentian.xiangkan.ContentListAdapter
import com.chentian.xiangkan.R

/**
 * 主页
 */
class HomeFragment : Fragment() {

    // region field

    private lateinit var contentList: RecyclerView
    private lateinit var contentListAdapter: ContentListAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var itemView: View
    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.layout_contentlist,container,false)
        initView()
        return itemView
    }

    private fun initView() {
        contentList = itemView.findViewById(R.id.content_list)
        contentListAdapter = ContentListAdapter()
        contentList.adapter = contentListAdapter
        contentList.layoutManager = LinearLayoutManager(activity)
    }
}