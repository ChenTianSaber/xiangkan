package com.chentian.xiangkan.workmanager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.chentian.xiangkan.MainActivity
import com.chentian.xiangkan.data.ResponseCode
import com.chentian.xiangkan.utils.AppUtils

class UpdateDataWork(private val appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    companion object{
        const val TAG = "UpdateDataWork"
    }

    override fun doWork(): Result {

        // TODO(更新订阅数据)
        Log.d(TAG, "doWork: UpdateDataWork")

        AppUtils.getCurrentActivity()?.let {
            (it as MainActivity).rssLinkRepository.getAllRssLinkInfo(ResponseCode.GET_RSSLINK_SUCCESS_NEED_REQUEST)
        }

        // 返回结果
        return Result.success()
    }
}