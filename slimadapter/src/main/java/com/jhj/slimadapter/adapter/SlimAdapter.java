package com.jhj.slimadapter.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.jhj.slimadapter.callback.ItemViewCallback;
import com.jhj.slimadapter.callback.ItemViewDelegate;
import com.jhj.slimadapter.holder.ViewInjector;
import com.jhj.slimadapter.model.MultiItemTypeModel;

import java.lang.reflect.Type;

/**
 * 简化Adapter
 * <p>
 * 支持单数据类型布局，多数据类型布局、多样式布局、Empty View、加载更多、以及添加标题和尾部
 * Created by jhj on 18-10-6.
 */

public class SlimAdapter extends BaseAdapter<SlimAdapter> {

    private SlimAdapter(RecyclerView.LayoutManager manager) {
        this.layoutManager = manager;
    }


    public static SlimAdapter creator(RecyclerView.LayoutManager manager) {
        return new SlimAdapter(manager);
    }

}
