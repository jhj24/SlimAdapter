package com.zgdj.slimadapterkt.listener

import android.view.View

import com.zgdj.slimadapterkt.BaseAdapter


/**
 * 自定义标题或尾部时自定义布局回调
 *
 *
 * Created by jhj on 18-10-11.
 */

interface OnCustomLayoutListener<T : BaseAdapter<T>> {
    fun onLayout(adapter: T, view: View)
}
