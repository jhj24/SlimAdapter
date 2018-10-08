package com.jhj.adapterdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by jhj on 18-10-6.
 */

public abstract class SlimViewHolder<D> extends RecyclerView.ViewHolder {

    private SparseArray<View> viewMap;
    private IViewInjector injector;

    public SlimViewHolder(View itemView) {
        super(itemView);
        viewMap = new SparseArray<>();
    }

    public SlimViewHolder(ViewGroup parent, int layoutRes) {
        this(LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false));
    }


    public void bind(D data, int pos) {
        if (injector == null) {
            injector = new DefaultViewInjector(this, pos);
        }
        onBind(data, injector);
    }

    @SuppressWarnings("unchecked")
    public final <V extends View> V getView(int id) {
        View view = viewMap.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            viewMap.put(id, view);
        }
        return (V) view;
    }


    abstract void onBind(D data, IViewInjector injector);

}
