package com.chentian.xiangkan.adapter

import android.content.Context
import android.graphics.Color
import android.text.format.DateFormat.getDateFormat
import android.text.format.Time
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chentian.xiangkan.R
import com.chentian.xiangkan.data.ResponseCode
import com.chentian.xiangkan.data.RssItem
import com.chentian.xiangkan.data.RssItemData
import com.chentian.xiangkan.listener.ItemClickListener
import com.chentian.xiangkan.utils.AppUtils
import com.chentian.xiangkan.utils.RssUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * 内容列表的Adapter
 */
class ContentListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TAG = "ContentListAdapter"
    }

    // region field

    private lateinit var context: Context
    private var dataList = mutableListOf<RssItem>()
    private var itemClick: ItemClickListener? = null
    private var contentListType: Int = ResponseCode.ALL

    // endregion

    // region api

    fun setDataList(dataList: MutableList<RssItem>) {
        this.dataList = dataList
    }

    fun getDataList(): MutableList<RssItem> {
        return this.dataList
    }

    fun setItemClick(itemClick: ItemClickListener) {
        this.itemClick = itemClick
    }

    fun isListEmpty(): Boolean {
        return dataList.isEmpty()
    }

    fun setContentListType(value: Int) {
        contentListType = value
    }

    // endregion

    // region override

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return getViewHolder(viewType = viewType, parent = parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        doBindViewHolder(getItemViewType(position), holder, position)
    }

    private fun onTextBindViewHolder(holder: TextContentViewHolder, position: Int) {
        val data = dataList[position]

        fun setupClickListener() {
            holder.itemView.setOnClickListener {
                itemClick?.onContentItemClick(itemView = holder.itemView, data = data)
            }

            holder.markReadBtn.setOnClickListener {
                itemClick?.onMarkReadClick(itemView = holder.itemView, data = data)
            }
        }

        fun setupUI() {
            // 设置标题和作者
            holder.title.text = data.title
            holder.author.text = data.author

//            // 设置描述
//            val pattern: Pattern = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE)
//            val matcher: Matcher = pattern.matcher(data.description)
//            holder.description.text = matcher.replaceAll("")

            // 设置日期
//            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE)
//            val date = Date(data.pubDate)
            holder.date.text = AppUtils.formatTime(data.pubDate)

//            // 设置右边的封面配图
//            if (data.imageUrl.isNullOrEmpty()) {
//                holder.cover.visibility = View.GONE
//            } else {
//                holder.cover.visibility = View.VISIBLE
//                Glide.with(context).load(data.imageUrl).into(holder.cover)
//            }

//            // 设置图标
//            Glide.with(context).load(data.icon).into(holder.icon)

            // 是否已读
            if (data.wasRead!!) {
                holder.title.setTextColor(Color.GRAY)
                holder.markReadBtn.text = "已阅"
                holder.markReadBtn.setTextColor(Color.GRAY)
            } else {
                holder.title.setTextColor(Color.BLACK)
                holder.markReadBtn.text = "未读"
                holder.markReadBtn.setTextColor(Color.BLACK)
            }

//            // TODO(判断是否是上次阅读的地方)
//            if (position != 0 && data.link == RssItemData.lastReadRssLink) {
//                holder.lastReadFlag.visibility = View.VISIBLE
//            } else {
//                holder.lastReadFlag.visibility = View.GONE
//            }

//            // 判断这个Item的日期是否是今天，然后判断上一个是不是
//            // 如果上一个没有或者上一个不是今天，那就显示出 今天 这个Text
//            fun isDateToday(data: RssItem): Boolean {
//                val time = Time("GTM+8")
//                time.set(data.pubDate)
//
//                val thenYear: Int = time.year
//                val thenMonth: Int = time.month
//                val thenMonthDay: Int = time.monthDay
//
//                time.set(System.currentTimeMillis())
//                return (thenYear == time.year
//                        && thenMonth == time.month
//                        && thenMonthDay == time.monthDay)
//            }
//
//            if (contentListType == ResponseCode.SINGLE && (isDateToday(data)) && (position == 0 || !isDateToday(
//                    dataList[position - 1]
//                ))
//            ) {
//                holder.outDate.visibility = View.VISIBLE
//            } else {
//                holder.outDate.visibility = View.GONE
//            }

        }

        setupClickListener()
        setupUI()
    }

    private fun onImageBindViewHolder(holder: ImageContentViewHolder, position: Int) {
        val data = dataList[position]

        fun setupClickListener() {
            holder.itemView.setOnClickListener {
                itemClick?.onContentItemClick(itemView = holder.itemView, data = data)
            }

            holder.markReadBtn.setOnClickListener {
                itemClick?.onMarkReadClick(itemView = holder.itemView, data = data)
            }
        }

        fun setupUI() {
            // 设置标题和作者
            holder.title.text = data.title
            holder.author.text = data.author

//            // 设置描述
//            val pattern: Pattern = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE)
//            val matcher: Matcher = pattern.matcher(data.description)
//            holder.description.text = matcher.replaceAll("")

            // 设置日期
//            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE)
//            val date = Date(data.pubDate)
            holder.date.text = AppUtils.formatTime(data.pubDate)

            // 设置左边的封面配图
            if (data.imageUrl.isNullOrEmpty()) {
                Glide.with(context).load(R.mipmap.bilibili).into(holder.cover)
            } else {
                Glide.with(context).load(data.imageUrl).into(holder.cover)
            }

//            // 设置图标
//            Glide.with(context).load(data.icon).into(holder.icon)

            // 是否已读
            if (data.wasRead!!) {
                holder.title.setTextColor(Color.GRAY)
                holder.markReadBtn.text = "已阅"
                holder.markReadBtn.setTextColor(Color.GRAY)
            } else {
                holder.title.setTextColor(Color.BLACK)
                holder.markReadBtn.text = "未读"
                holder.markReadBtn.setTextColor(Color.BLACK)
            }

//            // TODO(判断是否是上次阅读的地方)
//            if (position != 0 && data.link == RssItemData.lastReadRssLink) {
//                holder.lastReadFlag.visibility = View.VISIBLE
//            } else {
//                holder.lastReadFlag.visibility = View.GONE
//            }

//            // 判断这个Item的日期是否是今天，然后判断上一个是不是
//            // 如果上一个没有或者上一个不是今天，那就显示出 今天 这个Text
//            fun isDateToday(data: RssItem): Boolean {
//                val time = Time("GTM+8")
//                time.set(data.pubDate)
//
//                val thenYear: Int = time.year
//                val thenMonth: Int = time.month
//                val thenMonthDay: Int = time.monthDay
//
//                time.set(System.currentTimeMillis())
//                return (thenYear == time.year
//                        && thenMonth == time.month
//                        && thenMonthDay == time.monthDay)
//            }
//
//            if (contentListType == ResponseCode.SINGLE && (isDateToday(data)) && (position == 0 || !isDateToday(
//                    dataList[position - 1]
//                ))
//            ) {
//                holder.outDate.visibility = View.VISIBLE
//            } else {
//                holder.outDate.visibility = View.GONE
//            }

        }

        setupClickListener()
        setupUI()
    }

    private fun onVideoBindViewHolder(holder: VideoContentViewHolder, position: Int) {
        val data = dataList[position]

        fun setupClickListener() {
            holder.itemView.setOnClickListener {
                itemClick?.onContentItemClick(itemView = holder.itemView, data = data)
            }

            holder.markReadBtn.setOnClickListener {
                itemClick?.onMarkReadClick(itemView = holder.itemView, data = data)
            }
        }

        fun setupUI() {
            // 设置标题和作者
            holder.title.text = data.title
            holder.author.text = data.author

//            // 设置描述
//            val pattern: Pattern = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE)
//            val matcher: Matcher = pattern.matcher(data.description)
//            holder.description.text = matcher.replaceAll("")

            // 设置日期
//            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE)
//            val date = Date(data.pubDate)
            holder.date.text = AppUtils.formatTime(data.pubDate)

            // 设置左边的封面配图
            if (data.imageUrl.isNullOrEmpty()) {
                Glide.with(context).load(R.mipmap.bilibili).into(holder.cover)
            } else {
                Glide.with(context).load(data.imageUrl).into(holder.cover)
            }

//            // 设置图标
//            Glide.with(context).load(data.icon).into(holder.icon)

            // 是否已读
            if (data.wasRead!!) {
                holder.title.setTextColor(Color.GRAY)
                holder.markReadBtn.text = "已阅"
                holder.markReadBtn.setTextColor(Color.GRAY)
            } else {
                holder.title.setTextColor(Color.BLACK)
                holder.markReadBtn.text = "未读"
                holder.markReadBtn.setTextColor(Color.BLACK)
            }

//            // TODO(判断是否是上次阅读的地方)
//            if (position != 0 && data.link == RssItemData.lastReadRssLink) {
//                holder.lastReadFlag.visibility = View.VISIBLE
//            } else {
//                holder.lastReadFlag.visibility = View.GONE
//            }

//            // 判断这个Item的日期是否是今天，然后判断上一个是不是
//            // 如果上一个没有或者上一个不是今天，那就显示出 今天 这个Text
//            fun isDateToday(data: RssItem): Boolean {
//                val time = Time("GTM+8")
//                time.set(data.pubDate)
//
//                val thenYear: Int = time.year
//                val thenMonth: Int = time.month
//                val thenMonthDay: Int = time.monthDay
//
//                time.set(System.currentTimeMillis())
//                return (thenYear == time.year
//                        && thenMonth == time.month
//                        && thenMonthDay == time.monthDay)
//            }
//
//            if (contentListType == ResponseCode.SINGLE && (isDateToday(data)) && (position == 0 || !isDateToday(
//                    dataList[position - 1]
//                ))
//            ) {
//                holder.outDate.visibility = View.VISIBLE
//            } else {
//                holder.outDate.visibility = View.GONE
//            }

        }

        setupClickListener()
        setupUI()
    }

    override fun getItemViewType(position: Int): Int {
        return RssUtils.getViewTypeByChannelLink(dataList[position].channelLink)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    private fun getViewHolder(viewType: Int, parent: ViewGroup): RecyclerView.ViewHolder {
        return when (viewType) {
            RssUtils.VIEW_TYPE_TEXT -> TextContentViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_contentlist_text, parent, false)
            )
            RssUtils.VIEW_TYPE_IMAGE -> ImageContentViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_contentlist_image, parent, false)
            )
            RssUtils.VIEW_TYPE_VIDEO -> VideoContentViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_contentlist_video, parent, false)
            )
            else -> TextContentViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_contentlist_text, parent, false)
            )
        }
    }

    private fun doBindViewHolder(viewType: Int, holder: RecyclerView.ViewHolder, position: Int) {
        when (viewType) {
            RssUtils.VIEW_TYPE_TEXT -> onTextBindViewHolder(
                holder = holder as TextContentViewHolder,
                position = position
            )
            RssUtils.VIEW_TYPE_IMAGE -> onImageBindViewHolder(
                holder = holder as ImageContentViewHolder,
                position = position
            )
            RssUtils.VIEW_TYPE_VIDEO -> onVideoBindViewHolder(
                holder = holder as VideoContentViewHolder,
                position = position
            )
        }
    }

    // endregion

//    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val lastReadFlag: TextView = itemView.findViewById(R.id.last_read_flag)
//        val title: TextView = itemView.findViewById(R.id.title)
//        val description: TextView = itemView.findViewById(R.id.description)
//        val author: TextView = itemView.findViewById(R.id.author)
//        val date: TextView = itemView.findViewById(R.id.date)
//        val outDate: TextView = itemView.findViewById(R.id.out_date)
//        val icon: ImageView = itemView.findViewById(R.id.icon)
//        val cover: ImageView = itemView.findViewById(R.id.cover)
//        val markReadBtn: TextView = itemView.findViewById(R.id.mark_read_btn)
//    }

    /**
     * 当内容文本居多的话，使用这个ViewHolder
     */
    inner class TextContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val author: TextView = itemView.findViewById(R.id.author)
        val date: TextView = itemView.findViewById(R.id.date)
        val markReadBtn: TextView = itemView.findViewById(R.id.mark_read_btn)
    }

    /**
     * 当内容图片居多的话，使用这个ViewHolder
     */
    inner class ImageContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val author: TextView = itemView.findViewById(R.id.author)
        val date: TextView = itemView.findViewById(R.id.date)
        val cover: ImageView = itemView.findViewById(R.id.cover)
        val markReadBtn: TextView = itemView.findViewById(R.id.mark_read_btn)
    }

    /**
     * 当内容视频居多的话，使用这个ViewHolder
     */
    inner class VideoContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val author: TextView = itemView.findViewById(R.id.author)
        val date: TextView = itemView.findViewById(R.id.date)
        val cover: ImageView = itemView.findViewById(R.id.cover)
        val markReadBtn: TextView = itemView.findViewById(R.id.mark_read_btn)
    }
}