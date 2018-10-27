package com.jhj.slimadapter.itemdecoration;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by jhj on 18-10-26.
 */

public class ItemDivider extends RecyclerView.ItemDecoration {

    private int dividerWith = 10;
    private Paint paint;
    private RecyclerView.LayoutManager layoutManager;
    private boolean firstItemDecoration = false;
    private boolean endItemDecoration = false;

    public ItemDivider() {
        initPaint();
        paint.setColor(0xffff0000);
    }

    private void initPaint() {
        if (paint == null) {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(dividerWith);
        }
    }

    public ItemDivider setDividerWith(int dividerWith) {
        this.dividerWith = dividerWith;
        return this;
    }

    public ItemDivider setDividerColor(int color) {
        initPaint();
        paint.setColor(color);
        return this;
    }

    public ItemDivider setFirstItemDecoration(boolean topItemDecoration) {
        this.firstItemDecoration = topItemDecoration;
        return this;
    }

    public ItemDivider setEndItemDecoration(boolean bottomItemDecoration) {
        this.endItemDecoration = bottomItemDecoration;
        return this;
    }

    /**
     * 调用顺序 1
     * outRect.set(l,t,r,b)设置指定itemview的paddingLeft，paddingTop， paddingRight， paddingBottom
     *
     * @param outRect Rect to receive the output.
     * @param view    The child view to decorate
     * @param parent  RecyclerView this ItemDecoration is decorating
     * @param state   The current state of RecyclerView.
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (layoutManager == null) {
            layoutManager = parent.getLayoutManager();
        }
        // 适用 LinearLayoutManager 和 GridLayoutManager
        if (layoutManager instanceof LinearLayoutManager) {
            //横向布局、纵向布局
            int pos = parent.getChildViewHolder(view).getAdapterPosition();
            int count = parent.getAdapter().getItemCount();
            int orientation = ((LinearLayoutManager) layoutManager).getOrientation();
            if (orientation == LinearLayoutManager.VERTICAL) {
                if (firstItemDecoration && pos == 0) {
                    outRect.top = dividerWith;
                }
                if (endItemDecoration && pos == count - 1) {
                    outRect.bottom = dividerWith;
                } else if (pos >= 0 && pos < count - 1) {
                    //outRect.bottom = dividerWith;
                    outRect.set(0, 0, 0, dividerWith);
                }

            } else if (orientation == LinearLayoutManager.HORIZONTAL) {
                if (firstItemDecoration && pos == 0) {
                    outRect.left = dividerWith;
                }
                if (endItemDecoration && pos == count - 1) {
                    outRect.right = dividerWith;
                } else if (pos >= 0 && pos < count - 1) {
                    outRect.right = dividerWith;
                }
            }
            if (layoutManager instanceof GridLayoutManager) {
                GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                // 如果是 GridLayoutManager 则需要绘制另一个方向上的分割线
                if (orientation == LinearLayoutManager.VERTICAL && lp != null && lp.getSpanIndex() > 0) {
                    // 如果列表是垂直方向,则最左边的一列略过
                    outRect.left = dividerWith;
                }
                if (orientation == LinearLayoutManager.HORIZONTAL && lp != null && lp.getSpanIndex() > 0) {
                    // 如果列表是水平方向,则最上边的一列略过
                    outRect.top = dividerWith;
                }
            }
        }
    }

    /**
     * 通过一系列c.drawXXX()方法在绘制itemView之前绘制我们需要的内容  调用顺序 2
     * 一般分割线在这里绘制
     *
     * @param c      Canvas to draw into
     * @param parent RecyclerView this ItemDecoration is drawing into
     * @param state  The current state of RecyclerView
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        // 这个值是为了补偿横竖方向上分割线交叉处间隙
        if (layoutManager instanceof LinearLayoutManager) {
            int orientation = ((LinearLayoutManager) layoutManager).getOrientation();
            if (layoutManager instanceof GridLayoutManager) {
                drawHorizontalLine(c, parent);
                drawVerticalLine(c, parent);
            } else if (orientation == LinearLayoutManager.VERTICAL) {
                drawHorizontalLine(c, parent);
            } else if (orientation == LinearLayoutManager.HORIZONTAL) {
                drawVerticalLine(c, parent);
            }
        }

    }

    private void drawVerticalLine(Canvas c, RecyclerView parent) {

        if (firstItemDecoration) {
            View child = parent.getChildAt(0);
            int left = child.getLeft() - dividerWith / 2;
            c.drawLine(left, child.getTop(), left, child.getBottom(), paint);
        }

        if (endItemDecoration) {
            View child = parent.getChildAt(parent.getChildCount() - 1);
            int right = child.getRight() + dividerWith / 2;
            c.drawLine(right, child.getTop(), right, child.getBottom(), paint);
        }
        for (int i = 0; i < parent.getChildCount() - 1; i++) {
            View child = parent.getChildAt(i);
            int right = child.getRight() + dividerWith / 2;
            c.drawLine(right, child.getTop(), right, child.getBottom(), paint);
        }

    }


    private void drawHorizontalLine(Canvas c, RecyclerView parent) {
        if (firstItemDecoration) {
            View child = parent.getChildAt(0);
            int left = child.getLeft();
            int right = child.getRight();
            c.drawLine(left, child.getTop() - dividerWith / 2, right, child.getTop() - dividerWith / 2, paint);
        }

        if (endItemDecoration) {
            View child = parent.getChildAt(parent.getChildCount() - 1);
            int left = child.getLeft();
            int right = child.getRight();
            c.drawLine(left, child.getBottom() + dividerWith / 2, right, child.getBottom() + dividerWith / 2, paint);
        }
        for (int i = 0; i < parent.getChildCount() - 1; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getLeft();
            int right = child.getRight();
            c.drawLine(left, child.getBottom() + dividerWith / 2, right, child.getBottom() + dividerWith / 2, paint);
        }
    }

    /**
     * 在item 绘制之后调用(就是绘制在 item 的上层)  调用顺序 3
     * 也可以在这里绘制分割线,和上面的方法 二选一
     *
     * @param c      Canvas to draw into
     * @param parent RecyclerView this ItemDecoration is drawing into
     * @param state  The current state of RecyclerView
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

    }

    private int spanSize = 0;
    private int lastIndex = 0;

    /**
     * 获取pos位置在一行的下标
     *
     * @param recyclerView
     * @param pos
     * @return
     */
    public int getIndex(RecyclerView recyclerView, int pos) {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            int span = ((GridLayoutManager) manager).getSpanCount();
            int count = ((GridLayoutManager) manager).getSpanSizeLookup().getSpanSize(pos);
            if (spanSize == 0) {
                spanSize = count;
                lastIndex = 0;
                return 0;
            } else {
                if (spanSize + count > span) {
                    lastIndex = 0;
                    spanSize = count;
                    return 0;
                } else {
                    lastIndex++;
                    spanSize += count;
                    return lastIndex;
                }
            }
        }
        return 0;
    }
}
