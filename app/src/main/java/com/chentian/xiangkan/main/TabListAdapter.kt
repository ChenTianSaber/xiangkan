package com.chentian.xiangkan.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chentian.xiangkan.listener.ItemClickListener
import com.chentian.xiangkan.R
import com.chentian.xiangkan.data.RssLinkInfo
import com.chentian.xiangkan.utils.RssUtils

class TabListAdapter: RecyclerView.Adapter<TabListAdapter.TabViewHolder>() {

    private lateinit var context:Context
    var dataList = mutableListOf<RssLinkInfo>()
    var itemClick: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        context = parent.context
        return TabViewHolder(LayoutInflater.from(context).inflate(R.layout.item_tablist, parent,false))
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        holder.itemView.tag = position
        val data = dataList[position]

        holder.name.text = data.channelTitle

        if(data.icon.isNullOrEmpty()){
            Glide.with(context).load(RssUtils.getRSSIcon(data.channelLink)).into(holder.icon)
        } else {
            Glide.with(context).load(data.icon).into(holder.icon)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class TabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                itemClick?.onTabItemClick(itemView, dataList[itemView.tag as Int])
            }
        }
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val name: TextView = itemView.findViewById(R.id.name)
    }
}