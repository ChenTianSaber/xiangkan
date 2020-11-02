package com.chentian.xiangkan.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chentian.xiangkan.*

/**
 * 内容页
 */
class DetailFragment : Fragment() {

    companion object{
        const val TAG = "DetailFragment"
    }

    private lateinit var itemView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.layout_content,container,false)
        initView()
        initData()
        return itemView
    }

    private fun initView() {

    }

    private fun initData() {
        val data:RssItem = arguments?.get("RssItem") as RssItem
        Log.d(TAG, "initData: $data")
    }

}