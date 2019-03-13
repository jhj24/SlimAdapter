package com.zgdj.slimadapterkt.listener


import com.zgdj.slimadapterkt.BaseAdapter

/**
 * 加载更多
 *
 *
 * Created by jhj on 18-10-16.
 */

interface OnLoadMoreListener<T : BaseAdapter<T>> {

    fun onLoadMore(adapter: T)
}
