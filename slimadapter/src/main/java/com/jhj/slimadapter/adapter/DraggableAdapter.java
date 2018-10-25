package com.jhj.slimadapter.adapter;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.jhj.slimadapter.callback.ItemViewCallback;
import com.jhj.slimadapter.callback.ItemViewDelegate;
import com.jhj.slimadapter.holder.ViewInjector;
import com.jhj.slimadapter.itemtouch.ItemTouchHelperCallback;
import com.jhj.slimadapter.listener.OnItemDragListener;
import com.jhj.slimadapter.listener.OnItemSwipeListener;
import com.jhj.slimadapter.model.MultiItemTypeModel;

import java.lang.reflect.Type;
import java.util.Collections;

/**
 * 在　SlimAdapter 的基础上可以实现长按拖动，滑动删除（除了标题和结尾外的的布局）
 * <p>
 * Created by jhj on 18-10-23.
 */

public class DraggableAdapter extends BaseAdapter<DraggableAdapter> {

    private OnItemDragListener mOnItemDragListener;
    private OnItemSwipeListener mOnItemSwipeListener;
    private boolean itemDragEnabled = false;
    private boolean itemSwipeEnabled = false;
    private ItemTouchHelperCallback itemTouchHelperCallback;


    private DraggableAdapter(RecyclerView.LayoutManager manager) {
        this.layoutManager = manager;
    }

    public static DraggableAdapter creator(RecyclerView.LayoutManager manager) {
        return new DraggableAdapter(manager);
    }


    public DraggableAdapter setItemTouchHelper() {
        itemTouchHelperCallback = new ItemTouchHelperCallback(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(getRecyclerView());
        return this;
    }

    //======　drag　======

    public DraggableAdapter setDragItem(boolean isDrag) {
        this.itemDragEnabled = isDrag;
        return this;
    }

    public DraggableAdapter setDragFlags(int dragFlags) {
        itemTouchHelperCallback.setDragFlags(dragFlags);
        return this;
    }

    public DraggableAdapter setOnItemDragListener(OnItemDragListener onItemDragListener) {
        mOnItemDragListener = onItemDragListener;
        return this;
    }

    public DraggableAdapter setMoveThreshold(float moveThreshold) {
        itemTouchHelperCallback.setMoveThreshold(moveThreshold);
        return this;
    }

    //======= swipe =======


    public DraggableAdapter setSwipeItem(boolean isSwipe) {
        itemSwipeEnabled = isSwipe;
        return this;
    }

    public DraggableAdapter setSwipeFlags(int swipeFlags) {
        itemTouchHelperCallback.setSwipeFlags(swipeFlags);
        return this;
    }

    public DraggableAdapter setOnItemSwipeListener(OnItemSwipeListener listener) {
        mOnItemSwipeListener = listener;
        return this;
    }

    public DraggableAdapter setSwipeThreshold(float swipeThreshold) {
        itemTouchHelperCallback.setSwipeThreshold(swipeThreshold);
        return this;
    }


    //======　drag 监听器　======

    public void onItemDragStart(RecyclerView.ViewHolder viewHolder) {
        if (mOnItemDragListener != null && itemDragEnabled) {
            mOnItemDragListener.onItemDragStart(viewHolder, getViewHolderPosition(viewHolder));
        }
    }

    public void onItemDragMoving(RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        int from = getViewHolderPosition(source);
        int to = getViewHolderPosition(target);

        if (inRange(from) && inRange(to)) {
            if (from < to) {
                for (int i = from; i < to; i++) {
                    Collections.swap(getDataList(), i, i + 1);
                }
            } else {
                for (int i = from; i > to; i--) {
                    Collections.swap(getDataList(), i, i - 1);
                }
            }
            notifyItemMoved(source.getAdapterPosition(), target.getAdapterPosition());
        }

        if (mOnItemDragListener != null && itemDragEnabled) {
            mOnItemDragListener.onItemDragMoving(source, from, target, to);
        }
    }

    public void onItemDragEnd(RecyclerView.ViewHolder viewHolder) {
        if (mOnItemDragListener != null && itemDragEnabled) {
            mOnItemDragListener.onItemDragEnd(viewHolder, getViewHolderPosition(viewHolder));
        }
    }


    //======= swipe 监听器　=======


    public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder) {
        if (mOnItemSwipeListener != null && itemSwipeEnabled) {
            mOnItemSwipeListener.onItemSwipeStart(viewHolder, getViewHolderPosition(viewHolder));
        }
    }

    public void onItemSwipeClear(RecyclerView.ViewHolder viewHolder) {
        if (mOnItemSwipeListener != null && itemSwipeEnabled) {
            mOnItemSwipeListener.clearView(viewHolder, getViewHolderPosition(viewHolder));
        }
    }

    public void onItemSwiped(RecyclerView.ViewHolder viewHolder) {
        int pos = getViewHolderPosition(viewHolder);
        if (inRange(pos)) {
            getDataList().remove(pos);
            notifyItemRemoved(viewHolder.getAdapterPosition());
        }


        if (mOnItemSwipeListener != null && itemSwipeEnabled) {
            mOnItemSwipeListener.onItemSwiped(viewHolder, getViewHolderPosition(viewHolder));
        }
    }

    public void onItemSwiping(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
        if (mOnItemSwipeListener != null && itemSwipeEnabled) {
            mOnItemSwipeListener.onItemSwipeMoving(canvas, viewHolder, dX, dY, isCurrentlyActive);
        }
    }


    //========　其他　========

    public boolean isLongPressDragEnable() {
        return itemDragEnabled;
    }

    public boolean isItemSwipeEnable() {
        return itemSwipeEnabled;
    }

    private int getViewHolderPosition(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.getAdapterPosition() - getHeaderViewCount();
    }

    private boolean inRange(int position) {
        return position >= 0 && position < getDataList().size();
    }

}
