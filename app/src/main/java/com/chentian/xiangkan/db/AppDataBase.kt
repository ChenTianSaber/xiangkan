package com.chentian.xiangkan.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chentian.xiangkan.data.RssItem
import com.chentian.xiangkan.data.RssLinkInfo

@Database(entities = [RssLinkInfo::class, RssItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rssItemDao(): RssItemDao
    abstract fun rssLinkInfoDao(): RssLinkInfoDao
}