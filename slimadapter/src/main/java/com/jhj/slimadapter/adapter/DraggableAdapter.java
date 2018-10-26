package com.jhj.slimadapter.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.jhj.slimadapter.itemtouch.ItemTouchHelperCallback;
import com.jhj.slimadapter.listener.OnItemDragListener;
import com.jhj.slimadapter.listener.OnItemSwipeListener;

/**
 * 在　SlimAdapter 的基础上可以实现长按拖动，滑动删除（除了标题和结尾外的的布局）
 * <p>
 * Created by jhj on 18-10-23.
 */

public class DraggableAdapter extends BaseAdapter<DraggableAdapter> {

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
        itemTouchHelperCallback.setOnItemDragListener(onItemDragListener);
        return this;
    }

    public DraggableAdapter setMoveThreshold(float moveThreshold) {
        itemTouchHelperCallback.setMoveThreshold(moveThreshold);
        return this;
    }

    public boolean isLongPressDragEnable() {
        return itemDragEnabled;
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
        itemTouchHelperCallback.setOnItemSwipeListener(listener);
        return this;
    }

    public DraggableAdapter setSwipeThreshold(float swipeThreshold) {
        itemTouchHelperCallback.setSwipeThreshold(swipeThreshold);
        return this;
    }

    public DraggableAdapter setSwipeFadeOutAnim(boolean swipeFadeOutAnim) {
        itemTouchHelperCallback.setSwipeFadeOutAnim(swipeFadeOutAnim);
        return this;
    }

    public boolean isItemSwipeEnable() {
        return itemSwipeEnabled;
    }

}
