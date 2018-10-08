package com.jhj.adapterdemo.adapter;


import android.support.annotation.Nullable;



/**
 * Created by jhj on 18-10-6.
 */

public interface SlimInjector<T> {
     void onInject(@Nullable T data, @Nullable IViewInjector injector);
}
