package com.jhj.slimadapter.adapter;

/**
 * Created by jhj on 18-10-12.
 */

public interface SlimInjector<T> {

    int getItemViewLayoutId();

    void convert(SlimViewHolder holder, T t, int position);

}
