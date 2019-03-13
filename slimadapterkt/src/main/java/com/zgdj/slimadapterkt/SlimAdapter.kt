package com.zgdj.slimadapterkt

import android.support.v7.widget.RecyclerView

/**
 * 简化Adapter
 *
 *
 * 支持单数据类型布局，多数据类型布局、多样式布局、Empty View、加载更多、以及添加标题和尾部
 * Created by jhj on 18-10-6.
 */

class SlimAdapter private constructor(manager: RecyclerView.LayoutManager) : BaseAdapter< SlimAdapter>() {

    init {
        this.layoutManager = manager
    }

    companion object {

        fun  creator(manager: RecyclerView.LayoutManager): SlimAdapter {
            return SlimAdapter(manager)
        }
    }

}
