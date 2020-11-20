package com.chentian.xiangkan.listener

import android.view.View
import com.chentian.xiangkan.data.RssItem
import com.chentian.xiangkan.data.RssLinkInfo

/**
 * 因为目前业务较少，所以只用一个Listener作为所有点击事件的集合
 */
interface ItemClickListener {
    /**
     * 当点击内容Item的时候
     */
    fun onContentItemClick(itemView: View, data: RssItem)

    /**
     * 当点击已读按钮时，将其标记为已读
     */
    fun onMarkReadClick(itemView: View, data: RssItem)

    /**
     * 当点击首页顶部TAB的时候
     */
    fun onTabItemClick(itemView: View, data: RssLinkInfo)

    /**
     * 当点击订阅管理页中订阅源的Item的时候
     */
    fun onManagerItemClick(itemView: View, data: RssLinkInfo)
}