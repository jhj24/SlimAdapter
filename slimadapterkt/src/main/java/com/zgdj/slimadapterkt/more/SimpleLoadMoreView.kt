package com.zgdj.slimadapterkt.more


import com.zgdj.slimadapterkt.R

class SimpleLoadMoreView : LoadMoreView() {

    override val layoutId: Int
        get() = R.layout.layout_adapter_load_more

    override val loadingViewId: Int
        get() = R.id.load_more_loading_view

    override val loadFailViewId: Int
        get() = R.id.load_more_load_fail_view

    override val loadEndViewId: Int
        get() = R.id.load_more_load_end_view
}
