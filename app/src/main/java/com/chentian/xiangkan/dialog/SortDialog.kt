package com.chentian.xiangkan.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.chentian.xiangkan.MainActivity
import com.chentian.xiangkan.R
import com.chentian.xiangkan.data.ResponseCode
import com.chentian.xiangkan.data.RssLinkInfo
import com.chentian.xiangkan.data.RssLinkInfoFactory
import com.chentian.xiangkan.repository.RssItemRepository
import com.chentian.xiangkan.utils.RssUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_add_rsslinkinfo.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 筛选弹框
 */
class SortDialog : BottomSheetDialogFragment() {

    // region field

    private lateinit var sortAll: TextView
    private lateinit var sortRead: TextView
    private lateinit var sortUnRead: TextView

    // endregion

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return if(activity == null){
            super.onCreateDialog(savedInstanceState)
        }else{
            val root = LayoutInflater.from(activity).inflate(R.layout.dialog_sort,null)
            val dialog = BottomSheetDialog(activity!!)
            dialog.setContentView(root)
            initView(root)
            dialog
        }
    }

    private fun initView(root: View) {
        sortAll = root.findViewById(R.id.sort_all)
        sortRead = root.findViewById(R.id.sort_read)
        sortUnRead = root.findViewById(R.id.sort_unread)

        sortAll.setOnClickListener {
            Toast.makeText(activity,"全部",Toast.LENGTH_SHORT).show()
            (activity as MainActivity).changeSortType(RssItemRepository.SORT_ALL)
            dismiss()
        }

        sortRead.setOnClickListener {
            Toast.makeText(activity,"已读",Toast.LENGTH_SHORT).show()
            (activity as MainActivity).changeSortType(RssItemRepository.SORT_READ)
            dismiss()
        }

        sortUnRead.setOnClickListener {
            Toast.makeText(activity,"未读",Toast.LENGTH_SHORT).show()
            (activity as MainActivity).changeSortType(RssItemRepository.SORT_UNREAD)
            dismiss()
        }
    }

}