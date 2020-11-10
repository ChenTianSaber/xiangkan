package com.chentian.xiangkan.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chentian.xiangkan.*
import com.chentian.xiangkan.data.ResponseData
import com.chentian.xiangkan.data.RssLinkInfo
import com.chentian.xiangkan.MainActivity
import com.chentian.xiangkan.page.manager.ManagerListAdapter

/**
 * 订阅管理页Fragment
 * 这个页面的关注点只有订阅源的数据和订阅源的状态
 */
class ManagerFragment : Fragment() , View.OnClickListener{

    companion object{
        const val TAG = "ManagerFragment"
    }

    // region field

    private lateinit var itemView: View

    private lateinit var managerList: RecyclerView
    private lateinit var managerListAdapter: ManagerListAdapter

    // endregion

    /**
     * 下面是特定的创建订阅源的View
     */
    private lateinit var bilibiUp: LinearLayout // BiliBili up 的动态

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

        bilibiUp = itemView.findViewById(R.id.bilibili_up_rss)
        bilibiUp.setOnClickListener(this)
    }

    private fun initData() {

    }

    /**
     * 订阅源数据更新监听
     */
    fun onRssLinkInfoDataChanged(response: ResponseData) {
        Log.d(TAG, "onRssLinkInfoDataChanged ---> $response")
        // TODO(渲染对应的list)
    }

    /**
     * 监听Item的点击事件
     */
    fun onManagerItemClick(itemView: View, data: RssLinkInfo) {
        Log.d(TAG, "onManagerItemClick ---> $data")
        // TODO(更新Item的状态)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.bilibili_up_rss ->{
                // TODO(订阅B站up主)
            }
        }
    }

}