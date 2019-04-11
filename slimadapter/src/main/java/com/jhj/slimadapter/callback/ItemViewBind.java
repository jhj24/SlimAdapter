package com.jhj.slimadapter.callback;

import android.support.annotation.NonNull;

import com.jhj.slimadapter.SlimAdapter;
import com.jhj.slimadapter.holder.ViewInjector;

/**
 * 实现布局方法、数据、数据位置回调
 * <p>
 * Created by jhj on 18-10-13.
 */

public interface ItemViewBind<D> {

    void convert(SlimAdapter adapter, @NonNull ViewInjector injector, D bean, int position);

}
