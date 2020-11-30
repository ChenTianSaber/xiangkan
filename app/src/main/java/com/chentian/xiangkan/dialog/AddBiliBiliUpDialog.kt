package com.chentian.xiangkan.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import com.chentian.xiangkan.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/**
 * 添加BiliBili up主动态的弹框
 */
class AddBiliBiliUpDialog : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return if(activity == null){
            super.onCreateDialog(savedInstanceState)
        }else{
            val root = LayoutInflater.from(activity).inflate(R.layout.dialog_add_rsslinkinfo,null)
            val dialog = BottomSheetDialog(activity!!)
            dialog.setContentView(root)
            val params = root.layoutParams
            params.height = (1 * resources.displayMetrics.heightPixels).toInt()
            root.layoutParams = params
            dialog
        }
    }

}