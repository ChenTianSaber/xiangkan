package com.chentian.xiangkan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chentian.xiangkan.MainActivity
import com.chentian.xiangkan.listener.ItemClickListener
import com.chentian.xiangkan.R
import com.chentian.xiangkan.data.RssLinkInfo
import com.chentian.xiangkan.utils.RssUtils

/**
 * 首页顶部TAB列表的Adapter
 */
class TabListAdapter: RecyclerView.Adapter<TabListAdapter.TabViewHolder>() {

    companion object{
        const val TAG = "TabListAdapter"
    }

    // region field

    private lateinit var context: Context
    private var dataList = mutableListOf<RssLinkInfo>()
    private var itemClick: ItemClickListener? = null

    // endregion

    // region api

    fun setDataList(dataList: MutableList<RssLinkInfo>) {
        this.dataList = dataList
    }

    fun getDataList(): MutableList<RssLinkInfo> {
        return this.dataList
    }

    fun setItemClick(itemClick: ItemClickListener) {
        this.itemClick = itemClick
    }

    // endregion

    // region override

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        context = parent.context
        return TabViewHolder(LayoutInflater.from(context).inflate(R.layout.item_tablist, parent,false))
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {

        val data = dataList[position]

        /**
         * 点击事件
         */
        fun setupClickListener() {
            holder.itemView.setOnClickListener {
                itemClick?.onTabItemClick(itemView = holder.itemView, data = data)
            }
        }

        /**
         * 设置UI
         */
        fun setupUI() {
            // 设置名称
            holder.name.text = data.channelTitle

            // 设置图标
            if (data.isRefreshing) {
                Glide.with(context).load(R.mipmap.loading).into(holder.icon)
            } else {
//                if (data.icon.isEmpty()) {
//                    Glide.with(context).load(RssUtils.getRSSIcon(data.channelLink)).into(holder.icon)
//                } else {
//                    Glide.with(context).load(data.icon).into(holder.icon)
//                }
                RssUtils.setIcon(context, data.channelLink, holder.icon)
            }

            // TODO(设置选中状态，等设计完善了再搞)
//            if(data.isChoosed){
//                Glide.with(context).load(R.mipmap.ic_launcher).into(holder.icon)
//            }else{
//                Glide.with(context).load(data.icon).into(holder.icon)
//            }
        }

        setupClickListener()
        setupUI()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    // endregion

    inner class TabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val name: TextView = itemView.findViewById(R.id.name)
    }

}