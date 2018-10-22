package com.jhj.slimadapter.adapter;

import android.support.annotation.NonNull;

import com.jhj.slimadapter.holder.ViewInjector;

/**
 * 实现布局方法、数据、数据位置回调
 * <p>
 * Created by jhj on 18-10-13.
 */

public interface ItemViewCallback<D> {

    void convert(@NonNull ViewInjector injector,  D bean, @NonNull int position);

}
