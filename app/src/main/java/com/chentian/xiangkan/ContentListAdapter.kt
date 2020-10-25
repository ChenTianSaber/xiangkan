package com.chentian.xiangkan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ContentListAdapter : RecyclerView.Adapter<ContentListAdapter.ContentViewHolder>() {

    private lateinit var context:Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        context = parent.context
        return ContentViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_contentlist, parent,false)
        )
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 30
    }

    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}