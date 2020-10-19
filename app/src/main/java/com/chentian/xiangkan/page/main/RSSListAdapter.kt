package com.chentian.xiangkan.page.main

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chentian.xiangkan.R
import com.chentian.xiangkan.utils.RSSInfoUtils
import com.chentian.xiangkan.db.RSSItem
import java.text.SimpleDateFormat
import java.util.*

class RSSListAdapter : RecyclerView.Adapter<RSSListAdapter.MyViewHolder>() {

    var dataList: List<RSSItem> = mutableListOf()
    var itemClick: MainActivity.ItemClick? = null
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_view, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = dataList[position].title
        holder.author.text = dataList[position].author

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE)
        val date = dataList[position].pubDate?.plus(8 * 60 * 60 * 1000)?.let { Date(it) }//加8小时
        holder.date.text = simpleDateFormat.format(date)
        holder.itemView.tag = position
        dataList[position].channelLink?.let {
            RSSInfoUtils.getRSSIcon(it)
        }?.let {
            holder.icon.setImageResource(it)
        }

        //是否已读
        if(dataList[position].wasRead!!){
            context?.let { holder.title.setTextColor(ContextCompat.getColor(it,R.color.gray_2)) }
        }else{
            context?.let { holder.title.setTextColor(ContextCompat.getColor(it,R.color.black_0)) }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                itemClick?.onItemClick(itemView, dataList[itemView.tag as Int])
            }
        }

        val title: TextView = itemView.findViewById(R.id.title)
        val author: TextView = itemView.findViewById(R.id.author)
        val date: TextView = itemView.findViewById(R.id.date)
        val icon: ImageView = itemView.findViewById(R.id.icon)
    }
}