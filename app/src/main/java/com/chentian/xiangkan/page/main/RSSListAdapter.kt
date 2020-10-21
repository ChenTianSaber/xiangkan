package com.chentian.xiangkan.page.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chentian.xiangkan.R
import com.chentian.xiangkan.db.RSSItem
import com.chentian.xiangkan.utils.RSSInfoUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

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

        val pattern: Pattern = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(dataList[position].description)
        holder.description.text = matcher.replaceAll("")

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE)
        val date = dataList[position].pubDate?.let { Date(it) }
        holder.date.text = simpleDateFormat.format(date)
        Glide.with(context!!).load(dataList[position].imageUrl).into(holder.cover)
        holder.itemView.tag = position
        dataList[position].channelLink?.let {
            RSSInfoUtils.getRSSIcon(it)
        }?.let {
            Glide.with(context!!).load(it).circleCrop().into(holder.icon)
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
        val description: TextView = itemView.findViewById(R.id.description)
        val author: TextView = itemView.findViewById(R.id.author)
        val date: TextView = itemView.findViewById(R.id.date)
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val cover: ImageView = itemView.findViewById(R.id.cover)
    }
}