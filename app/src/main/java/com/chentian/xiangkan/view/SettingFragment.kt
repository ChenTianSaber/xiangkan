package com.chentian.xiangkan.view

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
import com.chentian.xiangkan.MainActivity
import com.chentian.xiangkan.adapter.ManagerListAdapter
import com.chentian.xiangkan.dialog.AddBiliBiliUpDialog

/**
 * 设置页
 */
class SettingFragment : Fragment() {

    companion object{
        const val TAG = "SettingFragment"
    }

    // region field

    private lateinit var itemView: View

    // endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.layout_setting,container,false)
        initView()
        initData()
        return itemView
    }

    private fun initView() {

    }

    private fun initData() {

    }

}