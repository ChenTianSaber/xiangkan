package com.chentian.xiangkan

import android.app.Application
import androidx.core.util.DebugUtils
import com.blankj.utilcode.util.AppUtils
import com.tencent.bugly.crashreport.CrashReport

class XiangKanApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        CrashReport.initCrashReport(this, "f1eb49d0fb", AppUtils.isAppDebug())
    }
}