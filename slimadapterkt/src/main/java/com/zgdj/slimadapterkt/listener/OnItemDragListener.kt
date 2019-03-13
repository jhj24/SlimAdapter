package com.zgdj.slimadapterkt.listener

import android.support.v7.widget.RecyclerView

/**
 * 拖拽事件
 * Created by jhj on 18-10-11.
 */
interface OnItemDragListener {

    /**
     * 可以设置开始动画
     *
     * @param viewHolder
     * @param pos
     */
    fun onItemDragStart(viewHolder: RecyclerView.ViewHolder, pos: Int)


    fun onItemDragMoving(source: RecyclerView.ViewHolder, from: Int, target: RecyclerView.ViewHolder, to: Int)

    /**
     * 设置结束动画
     *
     * @param viewHolder
     * @param pos
     */
    fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder, pos: Int)
}
