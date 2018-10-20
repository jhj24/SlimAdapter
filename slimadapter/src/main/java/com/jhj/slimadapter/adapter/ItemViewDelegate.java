package com.jhj.slimadapter.adapter;

import android.support.annotation.IdRes;

import com.jhj.slimadapter.holder.ViewInjector;

/**
 * 获取布局以及实现数据绑定
 * <p>
 * Created by jhj on 18-10-12.
 */

public interface ItemViewDelegate<T> {

    @IdRes
    int getItemViewLayoutId();

    void injector(ViewInjector injector, T t, int position);


}
