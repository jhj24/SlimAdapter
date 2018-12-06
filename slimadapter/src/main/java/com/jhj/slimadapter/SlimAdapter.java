package com.jhj.slimadapter;

import android.support.v7.widget.RecyclerView;

/**
 * 简化Adapter
 * <p>
 * 支持单数据类型布局，多数据类型布局、多样式布局、Empty View、加载更多、以及添加标题和尾部
 * Created by jhj on 18-10-6.
 */

public class SlimAdapter<T> extends BaseAdapter<T,SlimAdapter<T>> {

    public SlimAdapter(RecyclerView.LayoutManager manager) {
        this.layoutManager = manager;
    }


    public static SlimAdapter creator(RecyclerView.LayoutManager manager) {
        return new SlimAdapter(manager);
    }

}
