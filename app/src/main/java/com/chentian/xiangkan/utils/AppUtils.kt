package com.chentian.xiangkan.utils

import android.content.Context
import android.content.pm.ApplicationInfo


object AppUtils {
    /**
     * Return whether it is a debug application.
     *
     * @return `true`: yes<br></br>`false`: no
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