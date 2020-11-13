package com.chentian.xiangkan.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.chentian.xiangkan.R
import com.chentian.xiangkan.data.RssLinkInfoFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * 添加BiliBili up主动态的弹框
 */
class AddBiliBiliUpDialog(): BottomSheetDialogFragment() {

    // region field

    private lateinit var uidEditText: EditText // 输入框
    private lateinit var avatar: ImageView // 头像
    private lateinit var name: TextView // 名字
    private lateinit var searchBtn: TextView // 查找按钮
    private lateinit var addBtn: TextView // 确定添加按钮
    private lateinit var firstLayout: ConstraintLayout // 第一个页面
    private lateinit var secondLayout: ConstraintLayout // 第二个页面

    // endregion

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

    private fun initView(root: View) {
        uidEditText = root.findViewById(R.id.editText)
        searchBtn = root.findViewById(R.id.search)
        avatar = root.findViewById(R.id.icon)
        name = root.findViewById(R.id.name)
        addBtn = root.findViewById(R.id.add)
        firstLayout = root.findViewById(R.id.one_layout)
        secondLayout = root.findViewById(R.id.two_layout)

        searchBtn.setOnClickListener {
            // TODO(请求相关的信息，如果请求没有问题的话，那就展示下一步页面)
            if(uidEditText.text.toString().isEmpty()){
                Toast.makeText(activity,"uid不能为空~",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 用uid拼装rss链接，然后请求确认是否有数据
            val url = RssLinkInfoFactory.BILIBILI_UP + uidEditText.text.toString()

        }

        addBtn.setOnClickListener {
            // TODO(更新订阅源，关闭弹框)
        }
    }
}