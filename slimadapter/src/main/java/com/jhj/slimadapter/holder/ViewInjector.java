package com.jhj.slimadapter.holder;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * View的辅助方法
 * Created by jhj on 18-10-13.
 */

public class ViewInjector {

    private SlimViewHolder holder;

    public ViewInjector(SlimViewHolder holder) {
        this.holder = holder;
    }


    public <V extends View> V getView(int id) {
        return holder.getView(id);
    }

    public <V extends View> ViewInjector with(int id, ViewAction<V> action) {
        action.onAction((V) getView(id));
        return this;
    }

    public ViewInjector tag(int id, Object object) {
        getView(id).setTag(object);
        return this;
    }

    public ViewInjector text(int id, CharSequence charSequence) {
        TextView view = getView(id);
        view.setText(charSequence);
        return this;
    }

    public ViewInjector hint(int id, CharSequence charSequence) {
        TextView view = getView(id);
        view.setHint(charSequence);
        return this;
    }


    public ViewInjector textColor(int id, int color) {
        TextView view = getView(id);
        view.setTextColor(color);
        return this;
    }

    public ViewInjector textSize(int id, int sp) {
        TextView view = getView(id);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        return this;
    }


    public ViewInjector image(int id, int res) {
        ImageView view = getView(id);
        view.setImageResource(res);
        return this;
    }

    public ViewInjector image(int id, Drawable drawable) {
        ImageView view = getView(id);
        view.setImageDrawable(drawable);
        return this;
    }

    public ViewInjector background(int id, int res) {
        View view = getView(id);
        view.setBackgroundResource(res);
        return this;
    }

    @SuppressWarnings("deprecation")
    public ViewInjector background(int id, Drawable drawable) {
        View view = getView(id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
        return this;
    }

    public ViewInjector visible(int id) {
        getView(id).setVisibility(View.VISIBLE);
        return this;
    }

    public ViewInjector gone(int id) {
        getView(id).setVisibility(View.GONE);
        return this;
    }

    public ViewInjector visibility(int id, int visibility) {
        getView(id).setVisibility(visibility);
        return this;
    }


    public ViewInjector clicked(int id, View.OnClickListener listener) {
        getView(id).setOnClickListener(listener);
        return this;
    }

    public ViewInjector longClicked(int id, View.OnLongClickListener listener) {
        getView(id).setOnLongClickListener(listener);
        return this;
    }

    public ViewInjector enable(int id, boolean enable) {
        getView(id).setEnabled(enable);
        return this;
    }


    public ViewInjector checked(int id, boolean checked) {
        Checkable view = getView(id);
        view.setChecked(checked);
        return this;
    }


    public ViewInjector addView(int id, View... views) {
        ViewGroup viewGroup = getView(id);
        for (View view : views) {
            viewGroup.addView(view);
        }
        return this;
    }


    public ViewInjector removeAllViews(int id) {
        ViewGroup viewGroup = getView(id);
        viewGroup.removeAllViews();
        return this;
    }

    public ViewInjector removeView(int id, View view) {
        ViewGroup viewGroup = getView(id);
        viewGroup.removeView(view);
        return this;
    }



    public interface ViewAction<V extends View> {

        void onAction(V view);

    }
}
