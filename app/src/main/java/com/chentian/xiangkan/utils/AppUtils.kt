package com.chentian.xiangkan.utils

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.res.Resources
import android.os.Bundle
import android.text.format.Time
import androidx.fragment.app.Fragment
import com.chentian.xiangkan.MainActivity
import com.chentian.xiangkan.R
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*


object AppUtils {

    /**
     * 判断APP是否处于调试模式
     */
    fun isAppDebug(context:Context): Boolean {
        return try {
            val info: ApplicationInfo = context.applicationInfo
            info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        } catch (e: Exception) {
            false
        }
    }

    private var activityWeakRef: WeakReference<Activity>? = null

    fun getCurrentActivity(): Activity? {
        var currentActivity: Activity? = null
        activityWeakRef?.get()?.let {
            currentActivity = it
        }
        return currentActivity
    }

    fun setCurrentActivity(activity: Activity) {
        activityWeakRef = WeakReference(activity)
    }

    fun dp2px(dpValue: Float): Int{
        val scale = Resources.getSystem().displayMetrics.density
        return ((dpValue * scale + 0.5f).toInt())
    }

    /**
     * 格式化时间
     * 今天之内的显示 今天+时间
     * 昨天的显示 昨天+时间
     * 再往前的显示 日期+时间
     */
    fun formatTime(pubDate: Long): String {
        if(isDateToday(pubDate)){
            val simpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.CHINESE)
            val date = Date(pubDate)
            return "· 今天 ${simpleDateFormat.format(date)}"
        }

        if(isCurYear(pubDate)){
            val simpleDateFormat = SimpleDateFormat("MM-dd HH:mm:ss", Locale.CHINESE)
            val date = Date(pubDate)
            return "· ${simpleDateFormat.format(date)}"
        }

        // 设置日期
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE)
        val date = Date(pubDate)
        return "· ${simpleDateFormat.format(date)}"
    }

    /**
     * 判断是否是今天
     */
    fun isDateToday(pubDate: Long): Boolean {
        val time = Time("GTM+8")
        time.set(pubDate)

        val thenYear: Int = time.year
        val thenMonth: Int = time.month
        val thenMonthDay: Int = time.monthDay

        time.set(System.currentTimeMillis())
        return (thenYear == time.year
                && thenMonth == time.month
                && thenMonthDay == time.monthDay)
    }

    /**
     * 判断是否是今年
     */
    fun isCurYear(pubDate: Long): Boolean {
        val time = Time("GTM+8")
        time.set(pubDate)

        val thenYear: Int = time.year

        time.set(System.currentTimeMillis())
        return thenYear == time.year
    }

}