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
import com.chentian.xiangkan.adapter.ManagerListAdapter
import com.chentian.xiangkan.dialog.AddBiliBiliUpDialog

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
    private var managerListAdapter: ManagerListAdapter? = null

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
        managerListAdapter?.setItemClick(activity as MainActivity)

        bilibiUp = itemView.findViewById(R.id.bilibili_up_rss)
        bilibiUp.setOnClickListener(this)
    }

    private fun initData() {
        // 请求一下订阅源数据
        (activity as MainActivity).getRssLinks()
    }

    private fun refreshData(){
        managerListAdapter?.notifyDataSetChanged()
    }

    /**
     * 订阅源数据更新监听
     */
    fun onRssLinkInfoDataChanged(response: ResponseData) {
//        Log.d(TAG, "onRssLinkInfoDataChanged ---> $response")
        val dataList = response.data as MutableList<RssLinkInfo>
        val code = response.code
        val message = response.message

        Log.d(TAG, "onRssLinkInfoDataChanged ---> $code dataList--> ${dataList.size} message --> $message")

        // TODO(渲染对应的list)
        managerListAdapter?.let {
            it.setDataList(dataList)
            refreshData()
        }
    }

    /**
     * 监听Item的点击事件
     */
    fun onManagerItemClick(itemView: View, data: RssLinkInfo) {
        Log.d(TAG, "onManagerItemClick ---> $data")

        /**
         * 订阅这个源
         */
        fun handleRegisterRssLink(data: RssLinkInfo) {
            data.state = true
            refreshData()
        }

        /**
         * 取消订阅这个源
         * 将其state改为false
         * 更新列表
         */
        fun handleUnRegisterRssLink(data: RssLinkInfo) {
            data.state = false
            refreshData()
        }

        // TODO(更新Item的状态)
        when(data.state){
            true -> handleUnRegisterRssLink(data)
            false -> handleRegisterRssLink(data)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.bilibili_up_rss ->{
                // TODO(订阅B站up主)
                activity?.let {
                    AddBiliBiliUpDialog().show(it.supportFragmentManager,"bilibiliup")
                }
            }
        }
    }

}