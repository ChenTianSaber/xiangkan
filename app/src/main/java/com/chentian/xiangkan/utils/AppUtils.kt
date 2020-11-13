package com.chentian.xiangkan.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.chentian.xiangkan.MainActivity
import com.chentian.xiangkan.R


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

}