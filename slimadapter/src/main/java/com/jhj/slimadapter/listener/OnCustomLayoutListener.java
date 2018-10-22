package com.jhj.slimadapter.listener;

import android.view.View;

import com.jhj.slimadapter.adapter.SlimAdapter;

/**
 * 自定义标题或尾部时自定义布局回调
 * <p>
 * Created by jhj on 18-10-11.
 */

public interface OnCustomLayoutListener {
    void onLayout(SlimAdapter adapter, View view);
}
