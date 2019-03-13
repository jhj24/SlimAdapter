package com.zgdj.slimadapterkt

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

import com.zgdj.slimadapterkt.itemtouch.ItemTouchHelperCallback
import com.zgdj.slimadapterkt.listener.OnItemDragListener
import com.zgdj.slimadapterkt.listener.OnItemSwipeListener

/**
 * 在　SlimAdapter 的基础上可以实现长按拖动，滑动删除（除了标题和结尾外的的布局）
 *
 *
 * Created by jhj on 18-10-23.
 */

class DraggableAdapter private constructor(manager: RecyclerView.LayoutManager) : BaseAdapter<DraggableAdapter>() {

    var isLongPressDragEnable = false
        private set
    var isItemSwipeEnable = false
        private set
    private var itemTouchHelperCallback: ItemTouchHelperCallback? = null


    init {
        this.layoutManager = manager
    }

    fun setItemTouchHelper(): DraggableAdapter {
        itemTouchHelperCallback = ItemTouchHelperCallback(this)
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback!!)
        itemTouchHelper.attachToRecyclerView(getRecyclerView())
        return this
    }

    //======　drag　======


    fun setDragItem(isDrag: Boolean): DraggableAdapter {
        this.isLongPressDragEnable = isDrag
        return this
    }

    fun setDragFlags(dragFlags: Int): DraggableAdapter {
        itemTouchHelperCallback!!.setDragFlags(dragFlags)
        return this
    }

    fun setOnItemDragListener(onItemDragListener: OnItemDragListener): DraggableAdapter {
        itemTouchHelperCallback!!.setOnItemDragListener(onItemDragListener)
        return this
    }

    fun setMoveThreshold(moveThreshold: Float): DraggableAdapter {
        itemTouchHelperCallback!!.setMoveThreshold(moveThreshold)
        return this
    }

    //======= swipe =======


    fun setSwipeItem(isSwipe: Boolean): DraggableAdapter {
        isItemSwipeEnable = isSwipe
        return this
    }


    fun setSwipeFlags(swipeFlags: Int): DraggableAdapter {
        itemTouchHelperCallback!!.setSwipeFlags(swipeFlags)
        return this
    }

    fun setOnItemSwipeListener(listener: OnItemSwipeListener): DraggableAdapter {
        itemTouchHelperCallback!!.setOnItemSwipeListener(listener)
        return this
    }

    fun setSwipeThreshold(swipeThreshold: Float): DraggableAdapter {
        itemTouchHelperCallback!!.setSwipeThreshold(swipeThreshold)
        return this
    }

    fun setSwipeFadeOutAnim(swipeFadeOutAnim: Boolean): DraggableAdapter {
        itemTouchHelperCallback!!.setSwipeFadeOutAnim(swipeFadeOutAnim)
        return this
    }

    companion object {

        fun creator(manager: RecyclerView.LayoutManager): DraggableAdapter {
            return DraggableAdapter(manager)
        }
    }

}
