package com.zgdj.slimadapterkt.listener

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * 长按事件
 * Created by jhj on 18-10-11.
 */

interface OnItemLongClickListener {

    fun onItemLongClicked(recyclerView: RecyclerView, view: View, position: Int): Boolean

}
