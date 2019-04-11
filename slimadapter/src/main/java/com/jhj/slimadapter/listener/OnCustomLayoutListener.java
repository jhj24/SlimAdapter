package com.jhj.slimadapter.listener;

import android.view.View;

import com.jhj.slimadapter.SlimAdapter;

/**
 * 自定义标题或尾部时自定义布局回调
 * <p>
 * Created by jhj on 18-10-11.
 */

public interface OnCustomLayoutListener<T extends SlimAdapter> {
    void onLayout(T adapter, View view);
}
