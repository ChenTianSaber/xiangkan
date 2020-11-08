package com.chentian.xiangkan.page.manager

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

class ManagerListAdapter : RecyclerView.Adapter<ManagerListAdapter.ManagerViewHolder>() {

    private lateinit var context:Context
    var dataList = mutableListOf<RssLinkInfo>()
    var itemClick: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManagerViewHolder {
        context = parent.context
        return ManagerViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_managerlist, parent,false)
        )
    }

    override fun onBindViewHolder(holder: ManagerViewHolder, position: Int) {
        holder.itemView.tag = position
        if(dataList[position].url == "-1"){
            holder.name.text = ""
            Glide.with(context).load(RssUtils.getRSSIcon(dataList[position].channelLink)).into(holder.icon)
            holder.managerBtn.text = "添加订阅"
        }else{
            holder.name.text = dataList[position].channelTitle
            Glide.with(context).load(RssUtils.getRSSIcon(dataList[position].channelLink)).into(holder.icon)
            holder.managerBtn.text = if(dataList[position].state) "已订阅" else "未订阅"
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ManagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val managerBtn: TextView = itemView.findViewById(R.id.rss_manager_btn)
        val name: TextView = itemView.findViewById(R.id.name)
        val icon: ImageView = itemView.findViewById(R.id.icon)
        init {
            managerBtn.setOnClickListener {
                // 改变订阅状态
                itemClick?.onManagerItemClick(itemView, dataList[itemView.tag as Int])
            }
        }
    }
}