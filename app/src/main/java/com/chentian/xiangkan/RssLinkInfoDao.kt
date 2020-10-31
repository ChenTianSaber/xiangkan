package com.chentian.xiangkan

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RssLinkInfoDao {
    @Query("SELECT * FROM RssLinkInfo")
    fun getAll(): MutableList<RssLinkInfo>

    @Query("SELECT * FROM RssLinkInfo WHERE url = :url")
    fun getItemByUrl(url:String): MutableList<RssLinkInfo>

    @Insert
    fun insertItem(vararg rssManagerInfo: RssLinkInfo)

    @Update
    fun updateItems(vararg rssManagerInfo: RssLinkInfo)
}