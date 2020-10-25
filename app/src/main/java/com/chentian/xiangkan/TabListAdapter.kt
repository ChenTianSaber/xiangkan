package com.chentian.xiangkan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TabListAdapter: RecyclerView.Adapter<TabListAdapter.TabViewHolder>() {

    private lateinit var context:Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        context = parent.context
        return TabViewHolder(LayoutInflater.from(context).inflate(R.layout.item_tablist, parent,false))
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 10
    }

    inner class TabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}