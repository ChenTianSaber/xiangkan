package com.chentian.xiangkan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chentian.xiangkan.listener.ItemClickListener
import com.chentian.xiangkan.R
import com.chentian.xiangkan.data.RssLinkInfo
import com.chentian.xiangkan.utils.AppUtils
import com.chentian.xiangkan.utils.RssUtils

/**
 * 管理订阅源页面中自己创建列表的Adapter
 */
class UserCreateRssListAdapter : RecyclerView.Adapter<UserCreateRssListAdapter.ManagerViewHolder>() {

    companion object{
        const val TAG = "UserCreateRssListAdapter"
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
        return ManagerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_user_create_list, parent,false))
    }

    override fun onBindViewHolder(holder: ManagerViewHolder, position: Int) {
        val data = dataList[position]

        fun setupClickListener(){

        }

        fun setupUI(){
            when (position) {
                0 -> {
                    val layoutParams = holder.itemView.layoutParams
                    (layoutParams as ViewGroup.MarginLayoutParams).marginStart = AppUtils.dp2px(20f)
                    layoutParams.marginEnd = AppUtils.dp2px(0f)
                    holder.itemView.layoutParams = layoutParams
                }
                dataList.size - 1 -> {
                    val layoutParams = holder.itemView.layoutParams
                    (layoutParams as ViewGroup.MarginLayoutParams).marginStart = AppUtils.dp2px(0f)
                    layoutParams.marginEnd = AppUtils.dp2px(20f)
                    holder.itemView.layoutParams = layoutParams
                }
                else -> {
                    val layoutParams = holder.itemView.layoutParams
                    (layoutParams as ViewGroup.MarginLayoutParams).marginStart = AppUtils.dp2px(0f)
                    layoutParams.marginEnd = AppUtils.dp2px(0f)
                    holder.itemView.layoutParams = layoutParams
                }
            }

            holder.name.text = data.channelTitle
            Glide.with(context).load(data.icon).into(holder.icon)
        }

        setupClickListener()
        setupUI()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ManagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val iconLayout: CardView = itemView.findViewById(R.id.icon_layout)
    }
}