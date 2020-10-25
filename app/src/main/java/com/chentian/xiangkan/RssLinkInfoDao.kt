package com.chentian.xiangkan

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RssLinkInfoDao {
    @Query("SELECT * FROM RssLinkInfo")
    fun getAll(): MutableList<RssLinkInfo>

    @Insert
    fun insertAll(rssManagerInfos: MutableList<RssLinkInfo>)

    @Insert
    fun insertItem(vararg rssManagerInfo: RssLinkInfo)

    @Update
    fun updateItems(vararg rssManagerInfo: RssLinkInfo)
}