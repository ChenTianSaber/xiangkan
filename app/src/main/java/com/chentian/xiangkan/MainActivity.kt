package com.chentian.xiangkan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.chentian.xiangkan.page.AddFragment
import com.chentian.xiangkan.page.DetailFragment
import com.chentian.xiangkan.page.HomeFragment
import com.chentian.xiangkan.page.ManagerFragment

class MainActivity : AppCompatActivity() ,View.OnClickListener{

    // region field

    private lateinit var tabList: RecyclerView
    private lateinit var tabListAdapter: TabListAdapter

    private lateinit var homeFragment: HomeFragment
    private lateinit var detailFragment: DetailFragment
    private lateinit var managerFragment:ManagerFragment
    private lateinit var addFragment: AddFragment

    private lateinit var sortBtn:ImageView
    private lateinit var managerBtn:ImageView

    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)

        initView()
        initData()
    }

    private fun initView() {
        tabList = findViewById(R.id.tab_list)
        tabListAdapter = TabListAdapter()
        tabList.adapter = tabListAdapter
        tabList.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)

        homeFragment = HomeFragment()
        detailFragment = DetailFragment()
        managerFragment = ManagerFragment()
        addFragment = AddFragment()
        supportFragmentManager.beginTransaction().add(R.id.fragment_view,homeFragment).commit()

        sortBtn = findViewById(R.id.sort)
        sortBtn.setOnClickListener(this)
        managerBtn = findViewById(R.id.manager)
        managerBtn.setOnClickListener(this)
    }

    private fun initData() {

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.sort -> {
                ToastUtils.showShort("排序选项")
            }
            R.id.manager -> {
                ToastUtils.showShort("管理订阅")
                val transient = supportFragmentManager.beginTransaction().apply{
                    replace(R.id.fragment_view,managerFragment)
                    addToBackStack("HomeFragment")
                }
                transient.commit()
            }
        }
    }

}