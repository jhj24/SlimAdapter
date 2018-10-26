package com.jhj.slimadapter.itemtouch;

import android.graphics.Canvas;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.jhj.slimadapter.adapter.DraggableAdapter;
import com.jhj.slimadapter.listener.OnItemDragListener;
import com.jhj.slimadapter.listener.OnItemSwipeListener;

import java.util.Collections;

/**
 * 实现　recyclerView 可拖拽滑动
 * Created by jhj on 18-10-23.
 */

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private DraggableAdapter mAdapter;

    private OnItemDragListener dragListener;
    private OnItemSwipeListener swipeListener;

    private float moveThreshold = 0.1f;
    private float swipeThreshold = 0.5f;
    private int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    private int swipeFlags = ItemTouchHelper.LEFT;

    private boolean isDragging;
    private boolean isSwiping;
    private boolean isSwipeFadeOutAnim = true;


    public ItemTouchHelperCallback(DraggableAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public void setDragFlags(int dragFlags) {
        this.dragFlags = dragFlags;
    }


    public void setOnItemDragListener(OnItemDragListener listener) {
        this.dragListener = listener;
    }

    public void setMoveThreshold(float moveThreshold) {
        this.moveThreshold = moveThreshold;
    }


    public void setSwipeFlags(int swipeFlags) {
        this.swipeFlags = swipeFlags;
    }

    public void setSwipeFadeOutAnim(boolean swipeFadeOutAnim) {
        isSwipeFadeOutAnim = swipeFadeOutAnim;
    }

    public void setOnItemSwipeListener(OnItemSwipeListener listener) {
        this.swipeListener = listener;
    }

    public void setSwipeThreshold(float swipeThreshold) {
        this.swipeThreshold = swipeThreshold;
    }

    /**
     * 设置是否处理拖拽事件和滑动事件，以及拖拽和滑动操作的方向
     * <p>
     * 如果是列表类型的RecyclerView的只存在UP和DOWN，如果是网格类RecyclerView则还应该多有LEFT和RIGHT
     *
     * @param recyclerView recyclerView
     * @param viewHolder   viewHolder
     * @return 返回flag,{@link ItemTouchHelper.Callback#makeMovementFlags(int dragFlags, int swipeFlags)},
     * dragFlags 是拖拽标志，swipeFlags是滑动标志，我们把swipeFlags 都设置为0，表示不处理滑动操作。
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (!isBodyViewHolder(viewHolder)) {
            return makeMovementFlags(0, 0);
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }


    /**
     * 当传入的 dragFlags 不为０时，长按 item 的时候就会进入拖拽并在拖拽过程中不断回调 onMove() 方法
     *
     * @param recyclerView recyclerView
     * @param viewHolder   拖动的item
     * @param target       目标item
     * @return boolean
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return viewHolder.getItemViewType() == target.getItemViewType();
    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
        if (fromPos < toPos) {
            for (int i = fromPos; i < toPos; i++) {
                Collections.swap(mAdapter.getDataList(), i, i + 1);
            }
        } else {
            for (int i = fromPos; i > toPos; i--) {
                Collections.swap(mAdapter.getDataList(), i, i - 1);
            }
        }
        mAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        if (dragListener != null) {
            dragListener.onItemDragMoving(viewHolder, fromPos, target, toPos);
        }

    }

    /**
     * swipeFlags　不为０时，在滑动item的时候就会回调onSwiped的方法
     *
     * @param viewHolder 滑动item的ViewHolder
     * @param direction  方向
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (isBodyViewHolder(viewHolder)) {
            int pos = getViewHolderPosition(viewHolder);
            mAdapter.getDataList().remove(pos);
            mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            if (swipeListener != null) {
                swipeListener.onItemSwiped(viewHolder, pos);
            }

        }
    }


    /**
     * 当长按选中item的时候（拖拽开始的时候）调用
     *
     * @param viewHolder  viewHolder
     * @param actionState ACTION_STATE_IDLE：闲置状态
     *                    ACTION_STATE_SWIPE：滑动状态
     *                    ACTION_STATE_DRAG：拖拽状态
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && isBodyViewHolder(viewHolder)) {
            if (dragListener != null) {
                dragListener.onItemDragStart(viewHolder, getViewHolderPosition(viewHolder));
            }
            isDragging = true;
        } else if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && isBodyViewHolder(viewHolder)) {
            if (swipeListener != null) {
                swipeListener.onItemSwipeStart(viewHolder, getViewHolderPosition(viewHolder));
            }
            isSwiping = true;
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 用户操作完毕某个item并且其动画也结束后会调用该方法，一般我们在该方法内恢复ItemView的初始状态，防止由于复用而产生的显示错乱问题。
     *
     * @param recyclerView recyclerVIew
     * @param viewHolder   viewHolder
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (!isBodyViewHolder(viewHolder)) {
            return;
        }

        if (isDragging) {
            if (dragListener != null) {
                dragListener.onItemDragEnd(viewHolder, getViewHolderPosition(viewHolder));
            }
            isDragging = false;
        }
        if (isSwiping) {
            if (swipeListener != null) {
                swipeListener.clearView(viewHolder, getViewHolderPosition(viewHolder));
            }
            isSwiping = false;
        }

    }

    /**
     * 是否拖拽可用
     *
     * @return boolean
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return mAdapter.isLongPressDragEnable();
    }


    /**
     * 是否可滑动
     *
     * @return boolean
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return mAdapter.isItemSwipeEnable();
    }

    /**
     * 针对drag状态，滑动超过百分之多少的距离可以可以调用onMove()函数(注意哦，这里指的是onMove()函数的调用，并不是随手指移动的那个view哦)
     *
     * @param viewHolder viewHolder
     * @return float
     */
    @Override
    public float getMoveThreshold(RecyclerView.ViewHolder viewHolder) {
        return moveThreshold;
    }

    /**
     * swipe滑动的位置超过了百分之多少就消失
     *
     * @param viewHolder viewHolder
     * @return float
     */
    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return swipeThreshold;
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
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        // 判断当前是否是swipe方式：侧滑。
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && isBodyViewHolder(viewHolder)) {
            if (isSwipeFadeOutAnim()) {
                //1.ItemView--ViewHolder; 2.侧滑条目的透明度程度关联谁？dX(delta增量，范围：当前条目-width~width)。
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                float alpha = 1;
                if (layoutManager instanceof LinearLayoutManager) {
                    int orientation = ((LinearLayoutManager) layoutManager).getOrientation();
                    if (orientation == LinearLayoutManager.HORIZONTAL) {
                        alpha = 1 - Math.abs(dY) / viewHolder.itemView.getHeight();
                    } else if (orientation == LinearLayoutManager.VERTICAL) {
                        alpha = 1 - Math.abs(dX) / viewHolder.itemView.getWidth();
                    }
                }
                viewHolder.itemView.setAlpha(alpha);//1~0
            }
            if (swipeListener != null) {
                swipeListener.onItemSwipeMoving(c, viewHolder, dX, dY, isCurrentlyActive);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private boolean isSwipeFadeOutAnim() {
        return isSwipeFadeOutAnim;
    }

    private boolean isBodyViewHolder(RecyclerView.ViewHolder viewHolder) {
        int viewType = viewHolder.getItemViewType();
        return !mAdapter.isHeaderView(viewType) && !mAdapter.isFooterView(viewType) &&
                !mAdapter.isEmptyView(viewType) && !mAdapter.isLoadMoreView(viewType);

    }

    private int getViewHolderPosition(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.getAdapterPosition() - mAdapter.getHeaderViewCount();
    }

}
