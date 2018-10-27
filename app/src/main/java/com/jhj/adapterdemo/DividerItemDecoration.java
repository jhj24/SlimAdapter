package com.jhj.adapterdemo;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @Date 2016年8月29日
 * @describe RecyclerView Item间距
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;
    private Drawable mDivider;
    private int lastRawIndex = 0;

    public DividerItemDecoration(Context context) {
        this.mContext = context;
        mDivider = context.getResources().getDrawable(R.drawable.divider_decoration);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    //列数
    private int getSpanCount(RecyclerView parent) {
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    //画横线
    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();

            if (isFirstColumn(i))//如果是第一列
            {
                final int left = child.getLeft();
                final int right = child.getRight() + params.rightMargin
                        + mDivider.getIntrinsicWidth();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            } else if (isLastColumn(1))//如果是最后一列
            {
                final int left = child.getLeft();
                final int right = child.getRight();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            } else if (false) {

            } else {
                final int left = child.getLeft();
                final int right = child.getRight() + mDivider.getIntrinsicWidth();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    //画竖线
    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();

            if (i < getSpanCount(parent))//如果是第一行
            {
                final int top = child.getTop() + child.getPaddingTop();
                final int bottom = child.getBottom();
                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDivider.getIntrinsicWidth();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            } else if (isLastRaw(i))//如果是最后一行
            {
                final int top = child.getTop();
                final int bottom = child.getBottom() - child.getPaddingBottom();
                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDivider.getIntrinsicWidth();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            } else {
                final int top = child.getTop();
                final int bottom = child.getBottom();
                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDivider.getIntrinsicWidth();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }


    SparseIntArray intArray = new SparseIntArray();

    //设置每个item的偏移量，从而展示分割线
    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        if (itemPosition == 0) {
            getIndex(parent, 0, 0, 0, intArray);
            lastRawIndex = getLastRawIndex();
        }

        int marginRight = mDivider.getIntrinsicWidth();
        int marginBottom = mDivider.getIntrinsicHeight();

        if (isLastColumn(itemPosition) || itemPosition == parent.getAdapter().getItemCount() - 1) {// 如果是最后一列，则不需要绘制右边
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else if (isLastRaw(itemPosition)) {// 如果是最后一行，则不需要绘制底部
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        } else {
            outRect.set(0, 0, marginRight, marginBottom);
        }
    }


    private boolean isLastColumn(int index) {
        if (intArray.get(index + 1) == 0) {
            return true;
        }
        return false;
    }

    private boolean isFirstColumn(int index) {
        if (intArray.get(index) == 0) {
            return true;
        }
        return false;
    }

    private boolean isLastRaw(int index) {
        if (index >= lastRawIndex) {
            return true;
        } else {
            return false;
        }
    }

    //最后一行第一个item的index
    private int getLastRawIndex() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < intArray.size(); i++) {
            list.add(intArray.get(i));
        }
        return list.lastIndexOf(0);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 获取pos位置在一行的下标
     *
     * @param recyclerView
     * @param pos
     * @return
     */
    private void getIndex(RecyclerView recyclerView, int pos, int lastIndex, int spanSize, SparseIntArray intArray) {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        int size = recyclerView.getAdapter().getItemCount();
        if (manager instanceof GridLayoutManager && pos < size) {
            int span = ((GridLayoutManager) manager).getSpanCount();
            int count = ((GridLayoutManager) manager).getSpanSizeLookup().getSpanSize(pos);
            if (spanSize == 0) {
                spanSize = count;
                lastIndex = 0;
                intArray.put(pos, lastIndex);
                getIndex(recyclerView, pos + 1, lastIndex, spanSize, intArray);
            } else {
                if (spanSize + count > span) {
                    lastIndex = 0;
                    spanSize = count;
                    intArray.put(pos, lastIndex);
                    getIndex(recyclerView, pos + 1, lastIndex, spanSize, intArray);
                } else {
                    lastIndex++;
                    spanSize += count;
                    intArray.put(pos, lastIndex);
                    getIndex(recyclerView, pos + 1, lastIndex, spanSize, intArray);
                }
            }
        }
    }

}