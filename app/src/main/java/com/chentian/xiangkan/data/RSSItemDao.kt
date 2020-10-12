package com.chentian.xiangkan.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RSSItemDao {
    @Query("SELECT * FROM RSSItem")
    fun getAll(): List<RSSItem>

    @Insert
    fun insertAll(rssItems: List<RSSItem>)
}