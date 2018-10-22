package com.jhj.slimadapter.more;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;

import com.jhj.slimadapter.holder.SlimViewHolder;

public abstract class LoadMoreView {

    public static final int STATUS_DEFAULT = 1;
    public static final int STATUS_LOADING = 2;
    public static final int STATUS_FAIL = 3;
    public static final int STATUS_END = 4;

    private int mLoadMoreStatus = STATUS_DEFAULT;

    public void setLoadMoreStatus(int loadMoreStatus) {
        this.mLoadMoreStatus = loadMoreStatus;
    }

    public int getLoadMoreStatus() {
        return mLoadMoreStatus;
    }

    public void convert(SlimViewHolder holder) {
        switch (mLoadMoreStatus) {
            case STATUS_LOADING:
                visibleLoading(holder, true);
                visibleLoadFail(holder, false);
                visibleLoadEnd(holder, false);
                break;
            case STATUS_FAIL:
                visibleLoading(holder, false);
                visibleLoadFail(holder, true);
                visibleLoadEnd(holder, false);
                break;
            case STATUS_END:
                visibleLoading(holder, false);
                visibleLoadFail(holder, false);
                visibleLoadEnd(holder, true);
                break;
            case STATUS_DEFAULT:
                visibleLoading(holder, false);
                visibleLoadFail(holder, false);
                visibleLoadEnd(holder, false);
                break;
        }
    }

    private void visibleLoading(SlimViewHolder holder, boolean visible) {
        if (getLoadingViewId() != 0){
            holder.getViewInjector().visibility(getLoadingViewId(), visible ? View.VISIBLE : View.GONE);
        }

    }

    private void visibleLoadFail(SlimViewHolder holder, boolean visible) {
        if (getLoadFailViewId() != 0){
            holder.getViewInjector().visibility(getLoadFailViewId(), visible ? View.VISIBLE : View.GONE);
        }
    }

    private void visibleLoadEnd(SlimViewHolder holder, boolean visible) {
        if (getLoadEndViewId() != 0) {
            holder.getViewInjector().visibility(getLoadEndViewId(), visible ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * load more layout
     *
     * @return
     */
    public abstract @LayoutRes int getLayoutId();

    /**
     * loading view
     *
     * @return
     */
    protected abstract @IdRes int getLoadingViewId();

    /**
     * load fail view
     *
     * @return
     */
    protected abstract @IdRes int getLoadFailViewId();

    /**
     * load end view, you can return 0
     *
     * @return
     */
    protected abstract @IdRes int getLoadEndViewId();
}
