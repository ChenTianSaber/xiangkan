package com.chentian.xiangkan.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RSSManagerInfoDao {
    @Query("SELECT * FROM RSSManagerInfo")
    fun getAll(): MutableList<RSSManagerInfo>

    @Insert
    fun insertAll(rssManagerInfos: MutableList<RSSManagerInfo>)

    @Insert
    fun insertItem(vararg rssManagerInfo: RSSManagerInfo)

    @Update
    fun updateItems(vararg rssManagerInfo: RSSManagerInfo)
}