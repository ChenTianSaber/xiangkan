package com.chentian.xiangkan.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RSSItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rssItemDao(): RSSItemDao
}