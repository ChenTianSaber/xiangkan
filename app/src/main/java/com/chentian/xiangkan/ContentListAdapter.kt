package com.chentian.xiangkan

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class ContentListAdapter : RecyclerView.Adapter<ContentListAdapter.ContentViewHolder>() {

    private lateinit var context:Context
    var dataList = mutableListOf<RssItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        context = parent.context
        return ContentViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_contentlist, parent,false)
        )
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.title.text = dataList[position].title
        holder.author.text = dataList[position].author

        val pattern: Pattern = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(dataList[position].description)
        holder.description.text = matcher.replaceAll("")

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE)
        val date = Date(dataList[position].pubDate)
        holder.date.text = simpleDateFormat.format(date)

        if(dataList[position].imageUrl.isNullOrEmpty()){
            holder.cover.visibility = View.GONE
        }else{
            holder.cover.visibility = View.VISIBLE
            Glide.with(context).load(dataList[position].imageUrl).into(holder.cover)
        }

        //是否已读
        if(dataList[position].wasRead!!){
            context.let { holder.title.setTextColor(Color.GRAY) }
        }else{
            context.let { holder.title.setTextColor(Color.BLACK) }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val author: TextView = itemView.findViewById(R.id.author)
        val date: TextView = itemView.findViewById(R.id.date)
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val cover: ImageView = itemView.findViewById(R.id.cover)
    }
}