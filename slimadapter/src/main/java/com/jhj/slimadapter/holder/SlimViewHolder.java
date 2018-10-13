package com.jhj.slimadapter.holder;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * SlimViewHolder
 * <p>
 * Created by jhj on 18-10-6.
 */

public class SlimViewHolder extends RecyclerView.ViewHolder {

    private ViewInjector viewInjector;
    private SparseArray<View> viewMap;


    public SlimViewHolder(View itemView) {
        super(itemView);
        this.viewMap = new SparseArray<>();
        viewInjector = new ViewInjector(this);
    }

    public SlimViewHolder(ViewGroup parent, int layoutRes) {
        this(LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false));
    }

    /**
     * 通过viewId获取控件
     *
     * @param id  ViewIe
     * @param <V> <V extends View>
     * @return View
     */
    @SuppressWarnings("unchecked")
    public <V extends View> V getView(int id) {
        View view = viewMap.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            viewMap.put(id, view);
        }
        return (V) view;
    }

    public ViewInjector getViewInjector() {
        return viewInjector;
    }
}
