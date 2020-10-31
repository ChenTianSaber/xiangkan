package com.chentian.xiangkan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils

class TabListAdapter: RecyclerView.Adapter<TabListAdapter.TabViewHolder>() {

    private lateinit var context:Context
    var dataList = mutableListOf<RssLinkInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        context = parent.context
        return TabViewHolder(LayoutInflater.from(context).inflate(R.layout.item_tablist, parent,false))
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            ToastUtils.showShort(dataList[position].channelTitle)
        }
        holder.name.text = dataList[position].channelTitle
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class TabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val name: TextView = itemView.findViewById(R.id.name)
    }
}