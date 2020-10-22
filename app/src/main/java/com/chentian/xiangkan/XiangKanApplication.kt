package com.chentian.xiangkan

import android.app.Application
import androidx.core.util.DebugUtils
import com.blankj.utilcode.util.AppUtils
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure

class XiangKanApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashReport.initCrashReport(this, "f1eb49d0fb", AppUtils.isAppDebug())
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