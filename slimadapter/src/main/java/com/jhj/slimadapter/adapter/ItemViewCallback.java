package com.jhj.slimadapter.adapter;

import com.jhj.slimadapter.holder.ViewInjector;

/**
 * 实现布局方法、数据、数据位置回调
 * <p>
 * Created by jhj on 18-10-13.
 */

public interface ItemViewCallback<D> {

    void convert(ViewInjector holder, D t, int position);

}
