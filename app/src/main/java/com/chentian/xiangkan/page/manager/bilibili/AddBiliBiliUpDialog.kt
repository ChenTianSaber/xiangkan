package com.chentian.xiangkan.page.manager.bilibili

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.chentian.xiangkan.main.MainActivity
import com.chentian.xiangkan.R
import com.chentian.xiangkan.data.RssLinkInfo
import com.chentian.xiangkan.repository.RssRepository
import com.chentian.xiangkan.utils.RssUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AddBiliBiliUpDialog(private val rssRepository: RssRepository): BottomSheetDialogFragment() {

    private lateinit var editText: EditText // 输入框
    private lateinit var searchBtn: TextView // 查找按钮
    private lateinit var icon: ImageView // 头像
    private lateinit var name: TextView // 名字
    private lateinit var addBtn: TextView // 确定添加按钮

    private lateinit var oneLayout: ConstraintLayout // 第一个页面
    private lateinit var twoLayout: ConstraintLayout // 第二个页面

    private var rssLinkInfo: RssLinkInfo? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return if(activity == null){
            super.onCreateDialog(savedInstanceState)
        }else{
            val root = LayoutInflater.from(activity).inflate(R.layout.dialog_add_rsslinkinfo,null)
            val dialog = BottomSheetDialog(activity!!)
            dialog.setContentView(root)
            initView(root)
            dialog
        }
    }

    private fun initView(root:View){
        editText = root.findViewById(R.id.editText)
        searchBtn = root.findViewById(R.id.search)
        icon = root.findViewById(R.id.icon)
        name = root.findViewById(R.id.name)
        addBtn = root.findViewById(R.id.add)

        oneLayout = root.findViewById(R.id.one_layout)
        twoLayout = root.findViewById(R.id.two_layout)

        searchBtn.setOnClickListener {
            GlobalScope.launch (Dispatchers.IO){
                rssLinkInfo = RssUtils.addBiliBiliUpDynamic(editText.text.toString())
                rssLinkInfo?.let {rssLink->
                    GlobalScope.launch(Dispatchers.Main) {
                        Glide.with(this@AddBiliBiliUpDialog).load(rssLink.icon).into(icon)
                        name.text = rssLink.channelTitle
                        oneLayout.visibility = View.GONE
                        twoLayout.visibility = View.VISIBLE
                    }
                }
            }
        }

        addBtn.setOnClickListener {
            GlobalScope.launch (Dispatchers.IO){
                rssLinkInfo?.let {
                    Log.d(MainActivity.TAG, "onManagerItemClick: $it")
                    rssRepository.rssLinkInfoDao.insertItem(it)
                    rssRepository.getRssLinks()
                }
            }
        }
    }
}