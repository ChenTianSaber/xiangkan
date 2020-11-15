package com.chentian.xiangkan.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chentian.xiangkan.listener.ItemClickListener
import com.chentian.xiangkan.R
import com.chentian.xiangkan.data.RssItem
import com.chentian.xiangkan.utils.RssUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * 内容列表的Adapter
 */
class ContentListAdapter : RecyclerView.Adapter<ContentListAdapter.ContentViewHolder>() {

    companion object{
        const val TAG = "ContentListAdapter"
    }

    // region field

    private lateinit var context:Context
    private var dataList = mutableListOf<RssItem>()
    private var itemClick: ItemClickListener? = null

    // endregion

    // region api

    fun setDataList(dataList: MutableList<RssItem>) {
        this.dataList = dataList
    }

    fun setItemClick(itemClick: ItemClickListener) {
        this.itemClick = itemClick
    }

    fun isListEmpty(): Boolean {
        return dataList.isEmpty()
    }

    // endregion

    // region override

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        context = parent.context
        return ContentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_contentlist, parent,false))
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {

        val data = dataList[position]

        fun setupClickListener() {
            holder.itemView.setOnClickListener {
                itemClick?.onContentItemClick(itemView = holder.itemView, data = data)
            }
        }

        fun setupUI(){
            // 设置标题和作者
            holder.title.text = data.title
            holder.author.text = data.author

            // 设置描述
            val pattern: Pattern = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE)
            val matcher: Matcher = pattern.matcher(data.description)
            holder.description.text = matcher.replaceAll("")

            // 设置日期
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE)
            val date = Date(data.pubDate)
            holder.date.text = simpleDateFormat.format(date)

            // 设置右边的封面配图
            if(data.imageUrl.isNullOrEmpty()){
                holder.cover.visibility = View.GONE
            }else{
                holder.cover.visibility = View.VISIBLE
                Glide.with(context).load(data.imageUrl).into(holder.cover)
            }

            // 设置图标
            Glide.with(context).load(data.icon).into(holder.icon)

            // 是否已读
            if(data.wasRead!!){
                holder.title.setTextColor(Color.GRAY)
            }else{
                holder.title.setTextColor(Color.BLACK)
            }
        }

        setupClickListener()
        setupUI()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    // endregion

    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val author: TextView = itemView.findViewById(R.id.author)
        val date: TextView = itemView.findViewById(R.id.date)
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val cover: ImageView = itemView.findViewById(R.id.cover)
    }
}