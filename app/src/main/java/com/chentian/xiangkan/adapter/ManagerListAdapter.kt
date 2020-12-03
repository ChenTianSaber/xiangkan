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
 * 管理订阅源页面中默认订阅源列表的Adapter
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
            holder.managerBtnYes.setOnClickListener {
                itemClick?.onManagerItemClick(itemView = holder.itemView, data = data)
            }
            holder.managerBtnNo.setOnClickListener {
                itemClick?.onManagerItemClick(itemView = holder.itemView, data = data)
            }
        }

        fun setupUI(){
            holder.name.text = data.channelTitle
            Glide.with(context).load(data.icon).into(holder.icon)
            if (data.state){
                holder.managerBtnYes.visibility = View.VISIBLE
                holder.managerBtnNo.visibility = View.GONE
            }else{
                holder.managerBtnYes.visibility = View.GONE
                holder.managerBtnNo.visibility = View.VISIBLE
            }
        }

        setupClickListener()
        setupUI()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ManagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val managerBtnYes: TextView = itemView.findViewById(R.id.rss_manager_btn_yes)
        val managerBtnNo: TextView = itemView.findViewById(R.id.rss_manager_btn_no)
        val name: TextView = itemView.findViewById(R.id.name)
        val icon: ImageView = itemView.findViewById(R.id.icon)
    }
}