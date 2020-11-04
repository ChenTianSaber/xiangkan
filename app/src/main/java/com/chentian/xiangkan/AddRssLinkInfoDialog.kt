package com.chentian.xiangkan

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AddRssLinkInfoDialog: BottomSheetDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return if(activity == null){
            super.onCreateDialog(savedInstanceState)
        }else{
            val root = LayoutInflater.from(activity).inflate(R.layout.dialog_add_rsslinkinfo,null)
            val dialog = BottomSheetDialog(activity!!)
            dialog.setContentView(root)
            dialog
        }
    }
}