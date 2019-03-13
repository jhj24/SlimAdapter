package com.zgdj.slimadapterkt.itemdecoration

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * 线性布局分割线
 *
 *
 * Created by jhj on 18-10-26.
 */

class LineItemDecoration : RecyclerView.ItemDecoration() {

    private var dividerWith = 1
    private var paint: Paint? = null
    private var firstItemDecoration = false
    private var endItemDecoration = false

    init {
        initPaint()
        paint!!.color = -0x202021
    }

    private fun initPaint() {
        if (paint == null) {
            paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint!!.style = Paint.Style.FILL
            paint!!.strokeWidth = dividerWith.toFloat()
        }
    }

    fun setDividerWith(dividerWith: Int): LineItemDecoration {
        this.dividerWith = dividerWith
        return this
    }

    fun setDividerColor(color: Int): LineItemDecoration {
        initPaint()
        paint!!.color = color
        return this
    }

    fun setFirstItemDecoration(topItemDecoration: Boolean): LineItemDecoration {
        this.firstItemDecoration = topItemDecoration
        return this
    }

    fun setEndItemDecoration(bottomItemDecoration: Boolean): LineItemDecoration {
        this.endItemDecoration = bottomItemDecoration
        return this
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
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val layoutManager = parent.layoutManager
        if (layoutManager is LinearLayoutManager) {
            //横向布局、纵向布局
            val pos = parent.getChildViewHolder(view).adapterPosition
            val count = parent.adapter!!.itemCount
            val orientation = layoutManager.orientation
            if (orientation == LinearLayoutManager.VERTICAL) {
                if (firstItemDecoration && pos == 0) {
                    outRect.top = dividerWith
                }
                if (endItemDecoration && pos == count - 1) {
                    outRect.bottom = dividerWith
                }
                if (pos >= 0 && pos < count - 1) {
                    outRect.bottom = dividerWith
                }

            } else if (orientation == LinearLayoutManager.HORIZONTAL) {
                if (firstItemDecoration && pos == 0) {
                    outRect.left = dividerWith
                }
                if (endItemDecoration && pos == count - 1) {
                    outRect.right = dividerWith
                }
                if (pos >= 0 && pos < count - 1) {
                    outRect.right = dividerWith
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
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val layoutManager = parent.layoutManager
        if (layoutManager is LinearLayoutManager) {
            val orientation = layoutManager.orientation
            if (orientation == LinearLayoutManager.VERTICAL) {
                drawHorizontalLine(c, parent)
            } else if (orientation == LinearLayoutManager.HORIZONTAL) {
                drawVerticalLine(c, parent)
            }
        }

    }

    private fun drawVerticalLine(c: Canvas, parent: RecyclerView) {
        if (firstItemDecoration) {
            val child = parent.getChildAt(0)
            val topPos = child.top
            val bottomPos = child.bottom
            val horizontalPos = child.left - dividerWith / 2
            c.drawLine(horizontalPos.toFloat(), topPos.toFloat(), horizontalPos.toFloat(), bottomPos.toFloat(), paint!!)
        }
        for (i in 0 until parent.childCount) {
            if (!endItemDecoration && i == parent.childCount - 1) {
                return
            }
            val child = parent.getChildAt(i)
            val topPos = child.top
            val bottomPos = child.bottom
            val horizontalPos = child.right + dividerWith / 2
            c.drawLine(horizontalPos.toFloat(), topPos.toFloat(), horizontalPos.toFloat(), bottomPos.toFloat(), paint!!)
        }

    }


    private fun drawHorizontalLine(c: Canvas, parent: RecyclerView) {
        if (firstItemDecoration) {
            val child = parent.getChildAt(0)
            val leftPos = child.left
            val rightPos = child.right
            val verticalPos = child.top - dividerWith / 2
            c.drawLine(leftPos.toFloat(), verticalPos.toFloat(), rightPos.toFloat(), verticalPos.toFloat(), paint!!)
        }
        for (i in 0 until parent.childCount) {
            if (!endItemDecoration && i == parent.childCount - 1) {
                return
            }
            val child = parent.getChildAt(i)
            val leftPos = child.left
            val rightPos = child.right
            val verticalPos = child.bottom + dividerWith / 2
            c.drawLine(leftPos.toFloat(), verticalPos.toFloat(), rightPos.toFloat(), verticalPos.toFloat(), paint!!)
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
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

    }
}
