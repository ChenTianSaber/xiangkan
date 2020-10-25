package com.chentian.xiangkan

import android.app.Application
import com.blankj.utilcode.util.AppUtils
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure

class XiangKanApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // 初始化bugly
        CrashReport.initCrashReport(this, "f1eb49d0fb", AppUtils.isAppDebug())
        // 初始化友盟
        UMConfigure.init(
            this,
            "5f91a9f38a5de91db33dee20",
            "CHEN_TIAN",
            UMConfigure.DEVICE_TYPE_PHONE,
            null
        )
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
    }
}