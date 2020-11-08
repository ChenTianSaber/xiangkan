package com.chentian.xiangkan.listener

import android.view.View
import com.chentian.xiangkan.data.RssItem
import com.chentian.xiangkan.data.RssLinkInfo

interface ItemClickListener {
    fun onContentItemClick(itemView: View, data: RssItem)
    fun onTabItemClick(itemView: View, data: RssLinkInfo)
    fun onManagerItemClick(itemView: View, data: RssLinkInfo)
}