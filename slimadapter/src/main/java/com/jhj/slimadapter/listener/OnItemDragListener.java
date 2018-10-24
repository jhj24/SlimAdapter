package com.jhj.slimadapter.listener;

import android.support.v7.widget.RecyclerView;

/**
 * 拖拽事件
 * Created by jhj on 18-10-11.
 */
public interface OnItemDragListener {

    void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos);

    void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to);

    void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos);
}
