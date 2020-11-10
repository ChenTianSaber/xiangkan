package com.chentian.xiangkan.page.manager

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
import com.chentian.xiangkan.*
import com.chentian.xiangkan.data.ResponseData
import com.chentian.xiangkan.data.RssLinkInfo
import com.chentian.xiangkan.main.MainActivity
import com.chentian.xiangkan.page.manager.bilibili.AddBiliBiliUpDialog

/**
 * 订阅管理页
 */
class ManagerFragment : Fragment() {

    private lateinit var itemView: View

    private lateinit var managerList: RecyclerView
    private lateinit var managerListAdapter: ManagerListAdapter
    private lateinit var bilibiliUpRss: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.layout_manager,container,false)
        initView()
        initData()
        return itemView
    }

    private fun initView() {
        managerList = itemView.findViewById(R.id.manager_list)
        managerListAdapter = ManagerListAdapter()
        managerList.adapter = managerListAdapter
        managerList.layoutManager = LinearLayoutManager(activity)

        managerListAdapter.itemClick = activity as MainActivity

        bilibiliUpRss = itemView.findViewById(R.id.bilibili_up_rss)
        bilibiliUpRss.setOnClickListener {
            AddBiliBiliUpDialog((activity as MainActivity).rssRepository)
                .show((activity as MainActivity).supportFragmentManager, "addRssLinkInfo")
        }
    }

    private fun initData() {
        // 监听订阅源数据的变化
        (activity as MainActivity).rssModel.rssLinksData.observe(this, Observer<ResponseData>{ response ->
            Log.d(MainActivity.TAG, "rssLinksData observe ---> $response")
            managerListAdapter.dataList = response.data as MutableList<RssLinkInfo>
            managerListAdapter.notifyDataSetChanged()
        })
    }

    fun onManagerItemClick(itemView: View, data: RssLinkInfo) {
//        // 点击订阅管理的Item
//        if (data.url == "-1") {
//            Toast.makeText(this, "~", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(
//                this,
//                "${data.channelTitle} state --> ${data.state} ",
//                Toast.LENGTH_SHORT
//            ).show()
//            data.state = !data.state
//            rssRepository.updateRssLink(data)
//            rssRepository.getRssLinks()
//        }
    }

}