package com.chentian.xiangkan.page.manager

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chentian.xiangkan.R
import com.chentian.xiangkan.utils.RSSInfoUtils
import com.chentian.xiangkan.db.RSSItem
import com.chentian.xiangkan.db.RSSManagerInfo
import kotlinx.android.synthetic.main.item_manager.view.*
import java.text.SimpleDateFormat
import java.util.*

class ManagerListAdapter : RecyclerView.Adapter<ManagerListAdapter.MyViewHolder>() {

    companion object{
        const val TAG = "ManagerListAdapter"
    }

    var dataList: List<RSSManagerInfo> = mutableListOf()
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manager, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.text = dataList[position].name
        holder.state.isChecked = RSSInfoUtils.followRSSLink.contains(dataList[position].link)
        holder.itemView.tag = position
        Glide.with(context!!).load(RSSInfoUtils.getRSSIcon(dataList[position].channelLink)).into(holder.itemView.icon)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                val position = itemView.tag as Int
                if(RSSInfoUtils.followRSSLink.contains(dataList[position].link)){
                    RSSInfoUtils.followRSSLink.remove(dataList[position].link)
                }else{
                    RSSInfoUtils.followRSSLink.add(dataList[position].link)
                }
//                Log.d(TAG, "itemView.setOnClickListener: ${RSSInfoUtils.RSSLinkList}")
                notifyDataSetChanged()
            }
        }
        val name: TextView = itemView.findViewById(R.id.name)
        val state: SwitchCompat = itemView.findViewById(R.id.state)
        val icon:ImageView = itemView.findViewById(R.id.icon)
    }
}