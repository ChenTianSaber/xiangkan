package com.chentian.xiangkan.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.chentian.xiangkan.data.RssLinkInfo

@Dao
interface RssLinkInfoDao {
    /**
     * 获取所有RssLinkInfo
     */
    @Query("SELECT * FROM RssLinkInfo")
    fun getAll(): MutableList<RssLinkInfo>

    /**
     * 通过url获取RssLinkInfo
     */
    @Query("SELECT * FROM RssLinkInfo WHERE url = :url")
    fun getItemByUrl(url: String): MutableList<RssLinkInfo>

    @Insert
    fun insertItem(vararg rssManagerInfo: RssLinkInfo)

    @Update
    fun updateItems(vararg rssManagerInfo: RssLinkInfo)
}