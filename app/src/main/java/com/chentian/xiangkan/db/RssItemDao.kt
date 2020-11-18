package com.chentian.xiangkan.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.chentian.xiangkan.data.RssItem

@Dao
interface RssItemDao {
    /**
     * 根据id排序
     */
    @Query("SELECT * FROM RssItem ORDER BY id DESC")
    fun getAll(): MutableList<RssItem>

    /**
     * 根据时间逆序
     */
    @Query("SELECT * FROM RSSItem ORDER BY pubDate DESC")
    fun getAllOrderByPubDate(): MutableList<RssItem>

    /**
     * 查找所有未读
     */
    @Query("SELECT * FROM RSSItem WHERE wasRead = 0 ORDER BY pubDate DESC")
    fun getAllUnRead(): MutableList<RssItem>

    /**
     * 查找所有已读
     */
    @Query("SELECT * FROM RSSItem WHERE wasRead = 1 ORDER BY pubDate DESC")
    fun getAllWasRead(): MutableList<RssItem>

    /**
     * 根据url查找数据(时间逆序)
     */
    @Query("SELECT * FROM RssItem where url = :url ORDER BY pubDate DESC")
    fun getAllByUrlOrderByPubDate(url:String): MutableList<RssItem>

    /**
     * 根据url查找数据(未读，时间逆序)
     */
    @Query("SELECT * FROM RssItem where url = :url AND wasRead = 0 ORDER BY pubDate DESC")
    fun getAllByUrlOrderByPubDateUnRead(url:String): MutableList<RssItem>

    /**
     * 根据url查找数据(已读，时间逆序)
     */
    @Query("SELECT * FROM RssItem where url = :url AND wasRead = 1 ORDER BY pubDate DESC")
    fun getAllByUrlOrderByPubDateWasRead(url:String): MutableList<RssItem>

    /**
     * 根据url查找数据（id逆序）
     */
    @Query("SELECT * FROM RssItem where url = :url ORDER BY id DESC")
    fun getAllByUrl(url:String): MutableList<RssItem>

    /**
     * 根据title和author查找数据（id逆序）
     */
    @Query("SELECT * FROM RssItem where title = :title AND author = :author ORDER BY id DESC")
    fun getAllByTitleAndAuthor(title: String, author: String): MutableList<RssItem>

    @Insert
    fun insertAll(rssItems: MutableList<RssItem>)

    @Insert
    fun insertItem(vararg rssItem: RssItem)

    @Update
    fun updateItems(vararg rssItem: RssItem)
}