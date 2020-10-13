package com.chentian.xiangkan.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RSSItemDao {
    @Query("SELECT * FROM RSSItem")
    fun getAll(): MutableList<RSSItem>

    @Insert
    fun insertAll(rssItems: MutableList<RSSItem>)
}