package com.jhj.slimadapter.callback;

import android.support.annotation.NonNull;

import com.jhj.slimadapter.holder.ViewInjector;

/**
 * 实现布局方法、数据、数据位置回调
 * <p>
 * Created by jhj on 18-10-13.
 */

public abstract class ItemViewBind<D> {

    public abstract void convert(@NonNull ViewInjector injector, D bean, @NonNull int position);

}