package com.zgdj.slimadapterkt.more

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.view.View

import com.zgdj.slimadapterkt.holder.SlimViewHolder

abstract class LoadMoreView {

    var loadMoreStatus = STATUS_LOADING
    private var listener: OnLoadMoreFailClickClListener? = null

    /**
     * load more layout
     *
     * @return
     */
    @get:LayoutRes
    abstract val layoutId: Int

    /**
     * loading view
     *
     * @return
     */
    @get:IdRes
    protected abstract val loadingViewId: Int

    /**
     * load fail view
     *
     * @return
     */
    @get:IdRes
    protected abstract val loadFailViewId: Int

    /**
     * load end view, you can return 0
     *
     * @return
     */
    @get:IdRes
    protected abstract val loadEndViewId: Int

    fun convert(holder: SlimViewHolder) {
        when (loadMoreStatus) {
            STATUS_LOADING -> {
                visibleLoading(holder, true)
                visibleLoadFail(holder, false)
                visibleLoadEnd(holder, false)
            }
            STATUS_FAIL -> {
                visibleLoading(holder, false)
                visibleLoadFail(holder, true)
                visibleLoadEnd(holder, false)
            }
            STATUS_END -> {
                visibleLoading(holder, false)
                visibleLoadFail(holder, false)
                visibleLoadEnd(holder, true)
            }
        }
    }

    private fun visibleLoading(holder: SlimViewHolder, visible: Boolean) {
        if (loadingViewId != 0) {
            holder.viewInjector.visibility(loadingViewId, if (visible) View.VISIBLE else View.GONE)
        }

    }

    private fun visibleLoadFail(holder: SlimViewHolder, visible: Boolean) {
        if (loadFailViewId != 0) {
            holder.viewInjector.visibility(loadFailViewId, if (visible) View.VISIBLE else View.GONE)
            holder.viewInjector.clicked(loadFailViewId, View.OnClickListener {
                loadMoreStatus = STATUS_LOADING
                convert(holder)
                listener!!.onClicked()
            })
        }
    }

    private fun visibleLoadEnd(holder: SlimViewHolder, visible: Boolean) {
        if (loadEndViewId != 0) {
            holder.viewInjector.visibility(loadEndViewId, if (visible) View.VISIBLE else View.GONE)
        }
    }


    fun setLoadFailOnClickListener(listener: OnLoadMoreFailClickClListener) {
        this.listener = listener
    }

    interface OnLoadMoreFailClickClListener {
        fun onClicked()
    }

    companion object {
        const val STATUS_LOADING = 1
        const val STATUS_FAIL = 2
        const val STATUS_END = 3
    }
}
