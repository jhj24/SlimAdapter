package com.zgdj.slimadapterkt.callback

import com.zgdj.slimadapterkt.SlimAdapter
import com.zgdj.slimadapterkt.holder.ViewInjector


/**
 * 实现布局方法、数据、数据位置回调
 *
 *
 * Created by jhj on 18-10-13.
 */

interface ItemViewBind<in D> {

    fun convert(adapter: SlimAdapter, injector: ViewInjector, bean: D, position: Int)

}
