package com.jhj.adapterdemo;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * GridItemDecoration
 * 设置表格布局时，最好分割先的颜色同recyclerView的样色相同，这样的效果比较好看，因为当你设置spanSizeLookup时，不一定能出现想要的效果
 * <p>
 * Created by jhj on 18-10-27.
 */

public class ItemDivider extends RecyclerView.ItemDecoration {

    private Paint paint;
    private SparseIntArray intArray = new SparseIntArray();
    private int lastRawStartIndex = 0;
    private int dividerWith = 10;

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

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        int recyclerViewUsableWidth = parent.getWidth() - parent.getPaddingRight();
        for (int i = 0; i < childCount; i++) {
            View v = parent.getChildAt(i);
            if (v.getRight() != recyclerViewUsableWidth) {
                drawVertical(c, parent, i);
            }
            if (!isLastRaw(i)) {
                drawHorizontal(c, parent, i);
            }
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent, int i) {
        View child = parent.getChildAt(i);
        int recyclerViewUsableWidth = parent.getWidth() - parent.getPaddingRight();
        int left = child.getLeft();
        int right;
        if (child.getRight() == recyclerViewUsableWidth) {
            right = child.getRight();
        } else {
            right = child.getRight() + dividerWith;
        }
        c.drawLine(left, child.getBottom() + dividerWith / 2, right, child.getBottom() + dividerWith / 2, paint);

    }

    private void drawVertical(Canvas c, RecyclerView parent, int i) {
        View child = parent.getChildAt(i);
        int right = child.getRight() + dividerWith / 2;
        if (!isLastRaw(i)){
            c.drawLine(right, child.getTop(), right, child.getBottom(), paint);
        }
        if (isLastRaw(i) && parent.getAdapter().getItemCount() > i+1){
            c.drawLine(right, child.getTop(), right, child.getBottom(), paint);
        }

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int itemPosition = parent.getChildViewHolder(view).getAdapterPosition();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if (itemPosition == 0) {
                getIndex(parent, 0, 0, 0, intArray);
                lastRawStartIndex = getLastRawIndex();
            }
            int right = dividerWith;
            int bottom = dividerWith;
            if (isLastColumn(itemPosition)) {
                int theLineSpanCount = 0;
                for (int i = intArray.get(itemPosition); i >= 0; i--) {
                    theLineSpanCount += ((GridLayoutManager) layoutManager).getSpanSizeLookup().getSpanSize(itemPosition - i);
                }
                if (theLineSpanCount == ((GridLayoutManager) layoutManager).getSpanCount()) {
                    right = 0;
                } else {
                    right = 10;
                }
            }
            if (isLastRaw(itemPosition)) {
                bottom = 0;
            }
            outRect.set(0, 0, right, bottom);

        }
    }


    private boolean isFirstColumn(int index) {
        if (intArray.get(index) == 0) {
            return true;
        }
        return false;
    }

    private boolean isLastColumn(int index) {
        if (intArray.get(index + 1) == 0) {
            return true;
        }
        return false;
    }


    private boolean isLastRaw(int index) {
        if (index >= lastRawStartIndex) {
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
     * 设置pos位置的横向坐标
     *
     * @param recyclerView 　recyclerView
     * @param pos          　position
     * @param lastIndex    　上一个position的item在该行的位置（０、1、２...）
     * @param spanSize     　已经占据的比重
     * @param intArray     　存放index以及该item在该行的位置（０、1、２...）
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
