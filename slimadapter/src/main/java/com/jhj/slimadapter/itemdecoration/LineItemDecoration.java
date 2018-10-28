package com.jhj.slimadapter.itemdecoration;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 线性布局分割线
 * <p>
 * Created by jhj on 18-10-26.
 */

public class LineItemDecoration extends RecyclerView.ItemDecoration {

    private int dividerWith = 1;
    private Paint paint;
    private boolean firstItemDecoration = false;
    private boolean endItemDecoration = false;

    public LineItemDecoration() {
        initPaint();
        paint.setColor(0xffdfdfdf);
    }

    private void initPaint() {
        if (paint == null) {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(dividerWith);
        }
    }

    public LineItemDecoration setDividerWith(int dividerWith) {
        this.dividerWith = dividerWith;
        return this;
    }

    public LineItemDecoration setDividerColor(int color) {
        initPaint();
        paint.setColor(color);
        return this;
    }

    public LineItemDecoration setFirstItemDecoration(boolean topItemDecoration) {
        this.firstItemDecoration = topItemDecoration;
        return this;
    }

    public LineItemDecoration setEndItemDecoration(boolean bottomItemDecoration) {
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
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
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
                }
                if (pos >= 0 && pos < count - 1) {
                    outRect.bottom = dividerWith;
                }

            } else if (orientation == LinearLayoutManager.HORIZONTAL) {
                if (firstItemDecoration && pos == 0) {
                    outRect.left = dividerWith;
                }
                if (endItemDecoration && pos == count - 1) {
                    outRect.right = dividerWith;
                }
                if (pos >= 0 && pos < count - 1) {
                    outRect.right = dividerWith;
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
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            int orientation = ((LinearLayoutManager) layoutManager).getOrientation();
            if (orientation == LinearLayoutManager.VERTICAL) {
                drawHorizontalLine(c, parent);
            } else if (orientation == LinearLayoutManager.HORIZONTAL) {
                drawVerticalLine(c, parent);
            }
        }

    }

    private void drawVerticalLine(Canvas c, RecyclerView parent) {
        if (firstItemDecoration) {
            View child = parent.getChildAt(0);
            int topPos = child.getTop();
            int bottomPos = child.getBottom();
            int horizontalPos = child.getLeft() - dividerWith / 2;
            c.drawLine(horizontalPos, topPos, horizontalPos, bottomPos, paint);
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (!endItemDecoration && i == parent.getChildCount() - 1) {
                return;
            }
            View child = parent.getChildAt(i);
            int topPos = child.getTop();
            int bottomPos = child.getBottom();
            int horizontalPos = child.getRight() + dividerWith / 2;
            c.drawLine(horizontalPos, topPos, horizontalPos, bottomPos, paint);
        }

    }


    private void drawHorizontalLine(Canvas c, RecyclerView parent) {
        if (firstItemDecoration) {
            View child = parent.getChildAt(0);
            int leftPos = child.getLeft();
            int rightPos = child.getRight();
            int verticalPos = child.getTop() - dividerWith / 2;
            c.drawLine(leftPos, verticalPos, rightPos, verticalPos, paint);
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (!endItemDecoration && i == parent.getChildCount() - 1) {
                return;
            }
            View child = parent.getChildAt(i);
            int leftPos = child.getLeft();
            int rightPos = child.getRight();
            int verticalPos = child.getBottom() + dividerWith / 2;
            c.drawLine(leftPos, verticalPos, rightPos, verticalPos, paint);
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
}
