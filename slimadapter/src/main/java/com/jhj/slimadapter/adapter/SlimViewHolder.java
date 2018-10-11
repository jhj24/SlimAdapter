package com.jhj.slimadapter.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jhj on 18-10-6.
 */

public abstract class SlimViewHolder<D> extends RecyclerView.ViewHolder {

    private IViewInjector injector;

    private SparseArray<View> viewMap;


    public SlimViewHolder(View itemView) {
        super(itemView);
        viewMap = new SparseArray<>();
    }

    public SlimViewHolder(ViewGroup parent, int layoutRes) {
        this(LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false));
    }


    @SuppressWarnings("unchecked")
    public <V extends View> V getView(int id) {
        View view = viewMap.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            viewMap.put(id, view);
        }
        return (V) view;
    }


    public void bind(D data, int pos) {
        if (injector == null) {
            injector = new DefaultViewInjector(this);
        }
        onBind(data, injector, pos);
    }


    abstract void onBind(D data, IViewInjector injector, int pos);

}
