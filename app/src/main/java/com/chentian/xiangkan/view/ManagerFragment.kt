package com.chentian.xiangkan.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chentian.xiangkan.MainActivity
import com.chentian.xiangkan.R
import com.chentian.xiangkan.adapter.ManagerListAdapter
import com.chentian.xiangkan.adapter.UserCreateRssListAdapter
import com.chentian.xiangkan.data.ResponseData
import com.chentian.xiangkan.data.RssLinkInfo
import com.chentian.xiangkan.data.RssLinkInfoFactory
import com.chentian.xiangkan.dialog.AddBiliBiliUpDialog
import com.chentian.xiangkan.dialog.AddBiliBiliUpDialogBack
import com.chentian.xiangkan.utils.AppUtils

/**
 * 订阅管理页Fragment
 * 这个页面的关注点只有订阅源的数据和订阅源的状态
 */
class ManagerFragment : Fragment(), View.OnClickListener {

    companion object {
        const val TAG = "ManagerFragment"
    }

    // region field

    private lateinit var itemView: View

    private lateinit var managerList: RecyclerView
    private lateinit var managerListAdapter: ManagerListAdapter

    private lateinit var userCreateList: RecyclerView
    private lateinit var userCreateListAdapter: UserCreateRssListAdapter

    private lateinit var emptyLayout: LinearLayout

    // endregion

    /**
     * 下面是特定的创建订阅源的View
     */
    private lateinit var bilibiUp: ImageView // BiliBili up 的动态

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.layout_manager, container, false)
        initView()
        initData()
        return itemView
    }

    private fun initView() {
        emptyLayout = itemView.findViewById(R.id.empty_layout)

        managerList = itemView.findViewById(R.id.manager_list)
        managerListAdapter = ManagerListAdapter()
        managerList.adapter = managerListAdapter
        val layoutManager: LinearLayoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        managerList.layoutManager = layoutManager
        managerListAdapter.setItemClick(activity as MainActivity)

        userCreateList = itemView.findViewById(R.id.user_create_list)
        userCreateListAdapter = UserCreateRssListAdapter()
        userCreateList.adapter = userCreateListAdapter
        userCreateList.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)

        bilibiUp = itemView.findViewById(R.id.bilibili_up_rss)
        bilibiUp.setOnClickListener(this)
    }

    private fun initData() {
        onRssLinkInfoDataChanged()
    }

    private fun refreshData() {
        managerListAdapter.notifyDataSetChanged()
        userCreateListAdapter.notifyDataSetChanged()
    }

    /**
     * 订阅源数据更新监听
     */
    private fun onRssLinkInfoDataChanged() {
        (activity as MainActivity).rssModel.rssLinksData.observe(this, Observer<ResponseData> { response ->

            val dataList = response.data as MutableList<RssLinkInfo>
            val code = response.code
            val message = response.message

            Log.d(TAG, "onRssLinkInfoDataChanged:  code ---> [$code] dataList.size --> [${dataList.size}] message --> [$message]")

            // 渲染对应的list
            val defaultList = mutableListOf<RssLinkInfo>()
            val userCreateList = mutableListOf<RssLinkInfo>()
            for (data in dataList) {
                if (data.source == RssLinkInfoFactory.SOURCE_DEFAULT) {
                    defaultList.add(data)
                } else {
                    userCreateList.add(data)
                }
            }

            if (userCreateList.isEmpty()) {
                emptyLayout.visibility = View.VISIBLE
            } else {
                emptyLayout.visibility = View.GONE
            }


            // 设置列表的高度
            managerList.layoutParams.height = defaultList.size * AppUtils.dp2px(74f) + AppUtils.dp2px(24f)

            managerListAdapter.setDataList(defaultList)
            userCreateListAdapter.setDataList(userCreateList)
            refreshData()

        })
    }

    /**
     * 监听Item的点击事件
     */
    fun onManagerItemClick(itemView: View, data: RssLinkInfo) {
        Log.d(TAG, "onManagerItemClick ---> $data")

        /**
         * 订阅这个源
         * 将其state改为true
         * 更新列表
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

        // 更新Item的状态
        when (data.state) {
            true -> handleUnRegisterRssLink(data)
            false -> handleRegisterRssLink(data)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bilibili_up_rss -> {
                // 订阅B站up主
                activity?.let {
//                    AddBiliBiliUpDialog().show(it.supportFragmentManager, "bilibiliup")
                    AddBiliBiliUpDialogBack().show(it.supportFragmentManager, "bilibiliup")
                }
            }
        }
    }

}