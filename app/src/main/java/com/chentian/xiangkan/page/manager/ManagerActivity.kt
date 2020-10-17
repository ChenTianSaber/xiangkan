package com.chentian.xiangkan.page.manager

import android.graphics.Color
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chentian.xiangkan.R
import com.chentian.xiangkan.page.main.RSSListAdapter
import com.chentian.xiangkan.utils.RSSInfoUtils
import com.githang.statusbar.StatusBarCompat

class ManagerActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ManagerListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_manager)
        StatusBarCompat.setStatusBarColor(this, Color.WHITE,true)

        initView()
    }

    private fun initView(){
        viewManager = LinearLayoutManager(this)
        viewAdapter = ManagerListAdapter()
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
        viewAdapter.dataList = RSSInfoUtils.RSSLinkList
        viewAdapter.notifyDataSetChanged()
    }
}