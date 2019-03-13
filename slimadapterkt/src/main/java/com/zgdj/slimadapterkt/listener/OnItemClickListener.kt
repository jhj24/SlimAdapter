package com.zgdj.slimadapterkt.listener

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * 单击事件
 * Created by jhj on 18-10-11.
 */

interface OnItemClickListener {

    fun onItemClicked(recyclerView: RecyclerView, view: View, position: Int)

}
