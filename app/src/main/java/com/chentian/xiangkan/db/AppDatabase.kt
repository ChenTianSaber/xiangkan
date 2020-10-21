package com.chentian.xiangkan.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RSSItem::class,RSSManagerInfo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rssItemDao(): RSSItemDao
    abstract fun rssManagerInfoDao(): RSSManagerInfoDao
}