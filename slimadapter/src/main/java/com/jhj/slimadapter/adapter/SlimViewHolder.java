package com.jhj.slimadapter.adapter;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jhj on 18-10-6.
 */

public class SlimViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> viewMap;


    public SlimViewHolder(View itemView) {
        super(itemView);
        this.viewMap = new SparseArray<>();
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

    public View getItemView() {
        return itemView;
    }


    public <V extends View> SlimViewHolder with(int id, ViewAction<V> action) {
        action.onAction((V) getView(id));
        return this;
    }

    public SlimViewHolder tag(int id, Object object) {
        getView(id).setTag(object);
        return this;
    }

    public SlimViewHolder text(int id, CharSequence charSequence) {
        TextView view = getView(id);
        view.setText(charSequence);
        return this;
    }

    public SlimViewHolder hint(int id, CharSequence charSequence) {
        TextView view = getView(id);
        view.setHint(charSequence);
        return this;
    }


    public SlimViewHolder textColor(int id, int color) {
        TextView view = getView(id);
        view.setTextColor(color);
        return this;
    }

    public SlimViewHolder textSize(int id, int sp) {
        TextView view = getView(id);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        return this;
    }


    public SlimViewHolder image(int id, int res) {
        ImageView view = getView(id);
        view.setImageResource(res);
        return this;
    }

    public SlimViewHolder image(int id, Drawable drawable) {
        ImageView view = getView(id);
        view.setImageDrawable(drawable);
        return this;
    }

    public SlimViewHolder background(int id, int res) {
        View view = getView(id);
        view.setBackgroundResource(res);
        return this;
    }

    @SuppressWarnings("deprecation")
    public SlimViewHolder background(int id, Drawable drawable) {
        View view = getView(id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
        return this;
    }

    public SlimViewHolder visible(int id) {
        getView(id).setVisibility(View.VISIBLE);
        return this;
    }

    public SlimViewHolder gone(int id) {
        getView(id).setVisibility(View.GONE);
        return this;
    }

    public SlimViewHolder visibility(int id, int visibility) {
        getView(id).setVisibility(visibility);
        return this;
    }


    public SlimViewHolder clicked(int id, View.OnClickListener listener) {
        getView(id).setOnClickListener(listener);
        return this;
    }

    public SlimViewHolder longClicked(int id, View.OnLongClickListener listener) {
        getView(id).setOnLongClickListener(listener);
        return this;
    }

    public SlimViewHolder enable(int id, boolean enable) {
        getView(id).setEnabled(enable);
        return this;
    }


    public SlimViewHolder checked(int id, boolean checked) {
        Checkable view = getView(id);
        view.setChecked(checked);
        return this;
    }


    public SlimViewHolder addView(int id, View... views) {
        ViewGroup viewGroup = getView(id);
        for (View view : views) {
            viewGroup.addView(view);
        }
        return this;
    }


    public SlimViewHolder removeAllViews(int id) {
        ViewGroup viewGroup = getView(id);
        viewGroup.removeAllViews();
        return this;
    }

    public SlimViewHolder removeView(int id, View view) {
        ViewGroup viewGroup = getView(id);
        viewGroup.removeView(view);
        return this;
    }
}
