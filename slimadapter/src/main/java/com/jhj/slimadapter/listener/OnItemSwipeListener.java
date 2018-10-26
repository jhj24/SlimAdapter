package com.jhj.slimadapter.listener;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;

/**
 * 滑动事件
 * <p>
 * Created by jhj on 18-10-11.
 */
public interface OnItemSwipeListener {
    /**
     * 滑动动作开始时调用。(可设置开始动画)
     */
    void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos);

    /**
     * 滑动操作结束时调用。
     * 如果您在开始时更改视图，则应该重置此处，无论项目是否已刷过。
     *
     * @param pos 如果视图被刷过，则pos将为负数。
     */
    void clearView(RecyclerView.ViewHolder viewHolder, int pos);

    /**
     * 刷新项目时调用，视图将从适配器中删除。（可设置结束动画）
     */
    void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos);

    /**
     * 滑动移动时绘制
     *
     * @param canvas            画布
     * @param viewHolder        正在与用户进行交互的ViewHolder
     * @param dX                用户动作引起的水平位移量
     * @param dY                用户动作引起的垂直位移量
     * @param isCurrentlyActive 果此视图当前正由用户控制，则为True,false它只是简单地动画回原始状态。
     */
    void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive);
}
