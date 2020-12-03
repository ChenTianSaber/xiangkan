package com.chentian.xiangkan.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chentian.xiangkan.*
import com.chentian.xiangkan.MainActivity
import com.chentian.xiangkan.adapter.TabListAdapter
import com.chentian.xiangkan.adapter.ContentListAdapter
import com.chentian.xiangkan.data.*
import com.chentian.xiangkan.repository.RssLinkRepository
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 主页列表全部内容的fragment，之所以和普通的内容Fragment区分开是为了更方便的实现一些专门逻辑
 * 所有的数据操作都需要通过MainActivity来操作，不允许直接接触rssRepository
 */
class AllContentListFragment() : Fragment() {

    companion object {
        const val TAG = "AllContentListFragment"
    }

    constructor(rssLinkInfo: RssLinkInfo) : this() {
        this.rssLinkInfo = rssLinkInfo
    }

    // region field

    private lateinit var rssLinkInfo: RssLinkInfo

    private lateinit var contentList: RecyclerView
    private lateinit var contentListAdapter: ContentListAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var emptyLayout: LinearLayout
    private lateinit var updateTip: TextView
    private lateinit var itemView: View

    private var lastContentSize = 0
    private var lastWebContentData: MutableList<RssItem>? = null
    private var hasScrolled: Boolean = false

    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (savedInstanceState != null) {
            savedInstanceState.getParcelable<RssLinkInfo>("rssLinkInfo")?.let {
                this.rssLinkInfo = it
            }
        }
        itemView = inflater.inflate(R.layout.layout_contentlist,container,false)
        initView()
        initData()
        return itemView
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("rssLinkInfo", rssLinkInfo)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun initView() {
        contentList = itemView.findViewById(R.id.content_list)
        contentListAdapter = ContentListAdapter()
        contentListAdapter.setContentListType(ResponseCode.ALL)
        contentList.adapter = contentListAdapter
        contentList.layoutManager = LinearLayoutManager(activity)
        contentListAdapter.setItemClick(activity as MainActivity)

        swipeRefreshLayout = itemView.findViewById(R.id.swipe_refresh)
        swipeRefreshLayout.isEnabled = false

        emptyLayout = itemView.findViewById(R.id.empty_layout)
        updateTip = itemView.findViewById(R.id.update_tip)
        updateTip.setOnClickListener {
            // 点击之后刷新数据，列表回到顶部，然后把自己隐藏
            lastWebContentData?.let {
                contentListAdapter.setDataList(it)
                refreshData()
                lastContentSize = it.size
                lastWebContentData = null
                updateTip.visibility = View.GONE
                emptyLayout.visibility = View.GONE
            }
        }

        contentList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                //滑动结束
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 记录第一个可见的item，这就是上次阅读的位置
                    val position = (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                    if(position >= 0){
                        RssItemData.tempLastReadRssLink = contentListAdapter.getDataList()[position].link
                        Log.d(TAG, "onScrollStateChanged: RssItemData.tempLastReadRssLink --> [${RssItemData.tempLastReadRssLink}]")
                    }
                }
            }
        })
    }

    private fun initData() {
        Log.d(TAG, "initData: rssLinkInfo --> [$rssLinkInfo]")

        // 获取对应TAB下的数据
        (activity as MainActivity).getTabData(rssLinkInfo)
    }

    private fun refreshData(){
        contentListAdapter.notifyDataSetChanged()

        // 这边先判断是否已经自动滑动过了
        // 如果没有的话就自动跳到上次阅读到的位置e
        // TODO(这里每次自动跳太烦了，应该改为弹框让用户选择是否跳转)
//        if(!hasScrolled){
//            for(index in 0 until contentListAdapter.getDataList().size){
//                val data = contentListAdapter.getDataList()[index]
//                if(data.link == RssItemData.lastReadRssLink){
//                    contentList.smoothScrollToPosition(index)
//                    hasScrolled = true
//                    return
//                }
//            }
//        }
    }

    // region listen

    /**
     * 内容数据更新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRssItemDataChanged(response: ResponseData) {

        val dataList = response.data as MutableList<RssItem>
        val code = response.code
        val message = response.message
        val tag = response.tag

        Log.d(TAG, "onRssItemDataChanged code ---> [$code] dataList.size--> [${dataList.size}] message --> [$message] tag --> [$tag] lastContentSize--> [$lastContentSize] ")

        /**
         * 处理网络数据返回
         */
        fun handleWebResopnseSuccess(dataList: MutableList<RssItem>) {
            // 刷新标志取消，判断数据有无更新，有的话弹出更新tip，等点击tip再刷新列表，更新lastContentSize
            Toast.makeText(activity, "更新数据成功~", Toast.LENGTH_SHORT).show()
            val updateSum = dataList.size - lastContentSize
            if (updateSum > 0) {
                updateTip.text = "有 $updateSum 条更新"
                updateTip.visibility = View.VISIBLE
                lastWebContentData = dataList
            }
        }

        fun handleWebResopnseFail(dataList: MutableList<RssItem>) {
            // 失败的话就直接弹个Toast
            // TODO(这边需要把颗粒度做的更细致一点，例如 xx个更新成功 xx个更新失败 )
            Toast.makeText(activity, "获取订阅数据失败", Toast.LENGTH_SHORT).show()
            // 因为失败的话也可能是部分失败，所以还是要更新数据
            // 刷新标志取消，判断数据有无更新，有的话弹出更新tip，等点击tip再刷新列表，更新lastContentSize
            Toast.makeText(activity, "更新数据成功~", Toast.LENGTH_SHORT).show()
            val updateSum = dataList.size - lastContentSize
            if (updateSum > 0) {
                updateTip.text = "有 $updateSum 条更新"
                updateTip.visibility = View.VISIBLE
                lastWebContentData = dataList
            }
        }

        /**
         * 处理DB数据返回
         */
        fun handleDBResopnse(dataList: MutableList<RssItem>){
            // 先判断是不是这个TAB下的数据
            if(tag == ResponseCode.ALL){
                // 是的话DB数据直接展示
                contentListAdapter.setDataList(dataList)
                refreshData()
                // 记录下数量
                lastContentSize = dataList.size
            }
        }

        /**
         * 检查列表是否是空状态
         */
        fun checkIsListEmpty(){
            if (contentListAdapter.isListEmpty()) {
                emptyLayout.visibility = View.VISIBLE
            } else {
                emptyLayout.visibility = View.GONE
            }
        }

        when (code) {
            ResponseCode.WEB_SUCCESS -> handleWebResopnseSuccess(dataList)
            ResponseCode.WEB_FAIL -> handleWebResopnseFail(dataList)
            ResponseCode.DB_SUCCESS -> handleDBResopnse(dataList)
            ResponseCode.UPDATE_RSSITEM -> refreshData()
        }

        checkIsListEmpty()

    }

    // endregion

}