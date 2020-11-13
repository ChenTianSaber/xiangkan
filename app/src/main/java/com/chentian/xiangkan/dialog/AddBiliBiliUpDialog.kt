package com.chentian.xiangkan.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.chentian.xiangkan.MainActivity
import com.chentian.xiangkan.R
import com.chentian.xiangkan.data.ResponseCode
import com.chentian.xiangkan.data.RssLinkInfo
import com.chentian.xiangkan.data.RssLinkInfoFactory
import com.chentian.xiangkan.utils.RssUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_add_rsslinkinfo.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 添加BiliBili up主动态的弹框
 */
class AddBiliBiliUpDialog : BottomSheetDialogFragment() {

    // region field

    private lateinit var uidEditText: EditText // 输入框
    private lateinit var avatar: ImageView // 头像
    private lateinit var name: TextView // 名字
    private lateinit var searchBtn: TextView // 查找按钮
    private lateinit var addBtn: TextView // 确定添加按钮
    private lateinit var firstLayout: ConstraintLayout // 第一个页面
    private lateinit var secondLayout: ConstraintLayout // 第二个页面
    private lateinit var loadingView: FrameLayout // loading

    private var rssLinkInfo: RssLinkInfo? = null

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
        loadingView = root.findViewById(R.id.loading)

        searchBtn.setOnClickListener {

            rssLinkInfo = null

            // TODO(请求相关的信息，如果请求没有问题的话，那就展示下一步页面)
            val uid = uidEditText.text.toString()
            if(uid.isEmpty()){
                Toast.makeText(activity,"uid不能为空~",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showLoading()
            // 请求确认是否有数据
            GlobalScope.launch (Dispatchers.IO){
                rssLinkInfo = RssUtils.addBiliBiliUpDynamic(uid)
                GlobalScope.launch(Dispatchers.Main) {
                    hideLoading()
                    if(rssLinkInfo!!.channelTitle.isEmpty() || rssLinkInfo!!.channelLink.isEmpty() || rssLinkInfo!!.url.isEmpty()){
                        Toast.makeText(activity,"数据有误，请重试~",Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    // 填充下一步页面的数据，展示下一步页面
                    activity?.let { Glide.with(it).load(rssLinkInfo!!.icon).into(avatar) }
                    name.text = rssLinkInfo!!.channelTitle
                    firstLayout.visibility = View.GONE
                    secondLayout.visibility = View.VISIBLE
                }
            }

        }

        addBtn.setOnClickListener {
            // TODO(更新订阅源，关闭弹框)
            // 更新数据库，关闭弹框
            rssLinkInfo?.let {
                GlobalScope.launch {
                    (activity as MainActivity).insertRssLinkInfo(it)
                }
            }
            Toast.makeText(activity,"添加成功~",Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    private fun showLoading(){
        firstLayout.visibility = View.GONE
        loadingView.visibility = View.VISIBLE
    }

    private fun hideLoading(){
        firstLayout.visibility = View.VISIBLE
        loadingView.visibility = View.GONE
    }

}