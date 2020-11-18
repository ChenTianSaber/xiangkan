package com.chentian.xiangkan.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class UpdateDataWork(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {

        // TODO(更新订阅数据)

        // 返回结果
        return Result.success()
    }
}