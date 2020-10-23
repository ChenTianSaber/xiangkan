package com.chentian.xiangkan.page.manager

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chentian.xiangkan.MyEventBus
import com.chentian.xiangkan.R
import com.chentian.xiangkan.db.RSSManagerInfo
import com.chentian.xiangkan.page.add.AddActivity
import com.chentian.xiangkan.utils.RSSInfoUtils
import com.githang.statusbar.StatusBarCompat

class ManagerActivity : AppCompatActivity() , EventListener{

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ManagerListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var back: ImageView
    private lateinit var add: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_manager)
        StatusBarCompat.setStatusBarColor(this,  resources.getColor(R.color.white_3),true)

        MyEventBus.register(this)
        initView()
    }

    private fun initView(){
        back = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

        add = findViewById(R.id.add)
        add.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }

        viewManager = LinearLayoutManager(this)
        viewAdapter = ManagerListAdapter()
        viewAdapter.context = this
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
        viewAdapter.dataList = RSSInfoUtils.RSSLinkList
        viewAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        MyEventBus.unregister(this)
        super.onDestroy()
    }

    override fun addSuccess(rssManagerInfo:RSSManagerInfo) {
        viewAdapter.notifyDataSetChanged()
    }
}

interface EventListener{
    fun addSuccess(rssManagerInfo:RSSManagerInfo)
}