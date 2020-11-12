package com.chentian.xiangkan.adapter

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

/**
 * 管理订阅源页面中订阅源列表的Adapter
 */
class ManagerListAdapter : RecyclerView.Adapter<ManagerListAdapter.ManagerViewHolder>() {

    companion object{
        const val TAG = "ManagerListAdapter"
    }

    // region field

    private lateinit var context:Context
    private var dataList = mutableListOf<RssLinkInfo>()
    private var itemClick: ItemClickListener? = null

    // endregion

    // region api

    fun setDataList(dataList: MutableList<RssLinkInfo>) {
        this.dataList = dataList
    }

    fun setItemClick(itemClick: ItemClickListener) {
        this.itemClick = itemClick
    }

    // endregion

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManagerViewHolder {
        context = parent.context
        return ManagerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_managerlist, parent,false))
    }

    override fun onBindViewHolder(holder: ManagerViewHolder, position: Int) {
        val data = dataList[position]

        fun setupClickListener(){
            holder.managerBtn.setOnClickListener {
                itemClick?.onManagerItemClick(itemView = holder.itemView, data = data)
            }
        }

        fun setupUI(){
            holder.name.text = data.channelTitle
            holder.managerBtn.text = if (data.state) "已订阅" else "未订阅"
            if (data.icon.isEmpty()) {
                Glide.with(context).load(RssUtils.getRSSIcon(data.channelLink)).into(holder.icon)
            } else {
                Glide.with(context).load(data.icon).into(holder.icon)
            }
        }

        setupClickListener()
        setupUI()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ManagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val managerBtn: TextView = itemView.findViewById(R.id.rss_manager_btn)
        val name: TextView = itemView.findViewById(R.id.name)
        val icon: ImageView = itemView.findViewById(R.id.icon)
    }
}