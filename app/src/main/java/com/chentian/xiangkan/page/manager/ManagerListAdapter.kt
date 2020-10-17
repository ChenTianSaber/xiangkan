package com.chentian.xiangkan.page.manager

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chentian.xiangkan.R
import com.chentian.xiangkan.utils.RSSInfoUtils
import com.chentian.xiangkan.db.RSSItem
import com.chentian.xiangkan.db.RSSManagerInfo
import java.text.SimpleDateFormat
import java.util.*

class ManagerListAdapter : RecyclerView.Adapter<ManagerListAdapter.MyViewHolder>() {

    companion object{
        const val TAG = "ManagerListAdapter"
    }

    var dataList: List<RSSManagerInfo> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manager, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.text = dataList[position].name
        holder.state.setImageResource(if(dataList[position].state) R.mipmap.icon_sspai else R.mipmap.icon_round)
        holder.itemView.tag = position
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                val position = itemView.tag as Int
                dataList[position].state = !dataList[position].state
//                Log.d(TAG, "itemView.setOnClickListener: ${RSSInfoUtils.RSSLinkList}")
                notifyDataSetChanged()
            }
        }
        val name: TextView = itemView.findViewById(R.id.name)
        val state: ImageView = itemView.findViewById(R.id.state)
    }
}