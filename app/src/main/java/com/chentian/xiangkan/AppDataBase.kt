package com.chentian.xiangkan

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RssLinkInfo::class,RssItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rssItemDao(): RssItemDao
    abstract fun rssLinkInfoDao(): RssLinkInfoDao
}