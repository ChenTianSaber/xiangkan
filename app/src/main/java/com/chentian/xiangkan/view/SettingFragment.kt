package com.chentian.xiangkan.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.chentian.xiangkan.*

/**
 * 设置页
 */
class SettingFragment : Fragment() {

    companion object{
        const val TAG = "SettingFragment"
    }

    // region field

    private lateinit var itemView: View
    private lateinit var reportBtn: TextView

    // endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.layout_setting,container,false)
        initView()
        initData()
        return itemView
    }

    private fun initView() {
        reportBtn = itemView.findViewById(R.id.report)
        reportBtn.setOnClickListener {
            startActivity(Intent(activity, ReportActivity::class.java))
        }
    }

    private fun initData() {

    }

}