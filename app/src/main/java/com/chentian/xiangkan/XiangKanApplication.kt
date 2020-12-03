package com.chentian.xiangkan

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.chentian.xiangkan.utils.AppUtils
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure

class XiangKanApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        /**
         * 初始化bugly
         */
        CrashReport.initCrashReport(this, "f1eb49d0fb", AppUtils.isAppDebug(this))

        /**
         * 初始化友盟
         */
        UMConfigure.init(
            this,
            "5f91a9f38a5de91db33dee20",
            "CHEN_TIAN",
            UMConfigure.DEVICE_TYPE_PHONE,
            null
        )
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)

        /**
         * 管理Activity
         */
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks{
            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                AppUtils.setCurrentActivity(activity)
            }

            override fun onActivityResumed(activity: Activity) {
                AppUtils.setCurrentActivity(activity)
            }

        })

    }

}