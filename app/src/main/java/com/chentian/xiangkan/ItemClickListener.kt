package com.chentian.xiangkan

import android.view.View

interface ItemClickListener {
    fun onContentItemClick(itemView: View, data: RssItem)
    fun onTabItemClick(itemView: View, data: RssLinkInfo)
}