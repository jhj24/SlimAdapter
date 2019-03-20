package com.zgdj.slimadapterkt.itemtouch

import android.graphics.Canvas
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.zgdj.slimadapterkt.SlimAdapter
import com.zgdj.slimadapterkt.listener.OnItemDragListener
import com.zgdj.slimadapterkt.listener.OnItemSwipeListener
import java.util.*

/**
 * 实现　recyclerView 可拖拽滑动
 * Created by jhj on 18-10-23.
 */

class ItemTouchHelperCallback(private val mAdapter: SlimAdapter) : ItemTouchHelper.Callback() {

    private var dragListener: OnItemDragListener? = null
    private var swipeListener: OnItemSwipeListener? = null

    private var moveThreshold = 0.1f
    private var swipeThreshold = 0.5f
    private var dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    private var swipeFlags = ItemTouchHelper.LEFT

    private var isDragging: Boolean = false
    private var isSwiping: Boolean = false
    private var isSwipeFadeOutAnim = true

    fun setDragFlags(dragFlags: Int) {
        this.dragFlags = dragFlags
    }


    fun setOnItemDragListener(listener: OnItemDragListener) {
        this.dragListener = listener
    }

    fun setMoveThreshold(moveThreshold: Float) {
        this.moveThreshold = moveThreshold
    }


    fun setSwipeFlags(swipeFlags: Int) {
        this.swipeFlags = swipeFlags
    }

    fun setOnItemSwipeListener(listener: OnItemSwipeListener) {
        this.swipeListener = listener
    }

    fun setSwipeThreshold(swipeThreshold: Float) {
        this.swipeThreshold = swipeThreshold
    }

    fun setSwipeFadeOutAnim(isSwipeFadeOutAnim: Boolean) {
        this.isSwipeFadeOutAnim = isSwipeFadeOutAnim

    }

    /**
     * 设置是否处理拖拽事件和滑动事件，以及拖拽和滑动操作的方向
     *
     *
     * 如果是列表类型的RecyclerView的只存在UP和DOWN，如果是网格类RecyclerView则还应该多有LEFT和RIGHT
     *
     * @param recyclerView recyclerView
     * @param viewHolder   viewHolder
     * @return 返回flag,{@link ItemTouchHelper.Callback#makeMovementFlags(int dragFlags, int swipeFlags)},
     * dragFlags 是拖拽标志，swipeFlags是滑动标志，我们把swipeFlags 都设置为0，表示不处理滑动操作。
     */
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return if (!isBodyViewHolder(viewHolder)) {
            ItemTouchHelper.Callback.makeMovementFlags(0, 0)
        } else ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
    }


    /**
     * 当传入的 dragFlags 不为０时，长按 item 的时候就会进入拖拽并在拖拽过程中不断回调 onMove() 方法
     *
     * @param recyclerView recyclerView
     * @param viewHolder   拖动的item
     * @param target       目标item
     * @return boolean
     */
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return viewHolder.itemViewType == target.itemViewType
    }

    override fun onMoved(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, fromPos: Int, target: RecyclerView.ViewHolder, toPos: Int, x: Int, y: Int) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
        if (fromPos < toPos) {
            for (i in fromPos until toPos) {
                Collections.swap(mAdapter.getDataList(), i, i + 1)
            }
        } else {
            for (i in fromPos downTo toPos + 1) {
                Collections.swap(mAdapter.getDataList(), i, i - 1)
            }
        }
        mAdapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
        if (dragListener != null) {
            dragListener!!.onItemDragMoving(viewHolder, fromPos, target, toPos)
        }

    }

    /**
     * swipeFlags　不为０时，在滑动item的时候就会回调onSwiped的方法
     *
     * @param viewHolder 滑动item的ViewHolder
     * @param direction  方向
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (isBodyViewHolder(viewHolder)) {
            val pos = getViewHolderPosition(viewHolder)
            mAdapter.getDataList().toArrayList().removeAt(pos)
            mAdapter.notifyItemRemoved(viewHolder.adapterPosition)
            if (swipeListener != null) {
                swipeListener!!.onItemSwiped(viewHolder, pos)
            }

        }
    }


    /**
     * 当长按选中item的时候（拖拽开始的时候）调用
     *
     * @param viewHolder  viewHolder
     * @param actionState ACTION_STATE_IDLE：闲置状态
     * ACTION_STATE_SWIPE：滑动状态
     * ACTION_STATE_DRAG：拖拽状态
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && isBodyViewHolder(viewHolder!!)) {
            if (dragListener != null) {
                dragListener!!.onItemDragStart(viewHolder, getViewHolderPosition(viewHolder))
            }
            isDragging = true
        } else if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && isBodyViewHolder(viewHolder!!)) {
            if (swipeListener != null) {
                swipeListener!!.onItemSwipeStart(viewHolder, getViewHolderPosition(viewHolder))
            }
            isSwiping = true
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    /**
     * 用户操作完毕某个item并且其动画也结束后会调用该方法，一般我们在该方法内恢复ItemView的初始状态，防止由于复用而产生的显示错乱问题。
     *
     * @param recyclerView recyclerVIew
     * @param viewHolder   viewHolder
     */
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (!isBodyViewHolder(viewHolder)) {
            return
        }

        if (isDragging) {
            if (dragListener != null) {
                dragListener!!.onItemDragEnd(viewHolder, getViewHolderPosition(viewHolder))
            }
            isDragging = false
        }
        if (isSwiping) {
            if (swipeListener != null) {
                swipeListener!!.clearView(viewHolder, getViewHolderPosition(viewHolder))
            }
            isSwiping = false
        }

    }

    /**
     * 是否拖拽可用
     *
     * @return boolean
     */
    override fun isLongPressDragEnabled(): Boolean {
        return mAdapter.getLongPressDragEnable()
    }


    /**
     * 是否可滑动
     *
     * @return boolean
     */
    override fun isItemViewSwipeEnabled(): Boolean {
        return mAdapter.getItemSwipeEnable()
    }

    /**
     * 针对drag状态，滑动超过百分之多少的距离可以可以调用onMove()函数(注意哦，这里指的是onMove()函数的调用，并不是随手指移动的那个view哦)
     *
     * @param viewHolder viewHolder
     * @return float
     */
    override fun getMoveThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return moveThreshold
    }

    /**
     * swipe滑动的位置超过了百分之多少就消失
     *
     * @param viewHolder viewHolder
     * @return float
     */
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return swipeThreshold
    }

    /**
     * 交互规则或者自定义的动画效果
     *
     * @param c                 画布
     * @param recyclerView      recyclerView
     * @param viewHolder        正在与用户进行交互的ViewHolder
     * @param dX                用户动作引起的水平位移量
     * @param dY                用户动作引起的垂直位移量
     * @param actionState       动作（闲置、滑动、拖拽）
     * @param isCurrentlyActive 果此视图当前正由用户控制，则为True,false它只是简单地动画回原始状态。
     */
    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        // 判断当前是否是swipe方式：侧滑。
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && isBodyViewHolder(viewHolder)) {
            if (isSwipeFadeOutAnim) {
                //1.ItemView--ViewHolder; 2.侧滑条目的透明度程度关联谁？dX(delta增量，范围：当前条目-width~width)。
                val layoutManager = recyclerView.layoutManager
                var alpha = 1f
                if (layoutManager is LinearLayoutManager) {
                    val orientation = layoutManager.orientation
                    if (orientation == LinearLayoutManager.HORIZONTAL) {
                        alpha = 1 - Math.abs(dY) / viewHolder.itemView.height
                    } else if (orientation == LinearLayoutManager.VERTICAL) {
                        alpha = 1 - Math.abs(dX) / viewHolder.itemView.width
                    }
                }
                viewHolder.itemView.alpha = alpha//1~0
            }
            if (swipeListener != null) {
                swipeListener!!.onItemSwipeMoving(c, viewHolder, dX, dY, isCurrentlyActive)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun isBodyViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        val viewType = viewHolder.itemViewType
        return !mAdapter.isHeaderView(viewType) && !mAdapter.isFooterView(viewType) &&
                !mAdapter.isEmptyView(viewType) && !mAdapter.isLoadMoreView(viewType)

    }

    private fun getViewHolderPosition(viewHolder: RecyclerView.ViewHolder): Int {
        return viewHolder.adapterPosition - mAdapter.headerViewCount
    }

    private fun <T> List<T>?.toArrayList(): ArrayList<T> {
        return ArrayList(this.orEmpty())
    }

}
