package com.jhj.adapterdemo.adapter;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by jhj on 18-10-6.
 */
public class DefaultViewInjector implements IViewInjector<DefaultViewInjector> {

    private SlimViewHolder viewHolder;
    private int pos;

    public DefaultViewInjector(SlimViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    public DefaultViewInjector(SlimViewHolder viewHolder, int pos) {
        this.viewHolder = viewHolder;
        this.pos = pos;
    }

    @Override
    public final <T extends View> T findViewById(int id) {
        return (T) viewHolder.getView(id);
    }

    @Override
    public DefaultViewInjector tag(int id, Object object) {
        findViewById(id).setTag(object);
        return this;
    }

    @Override
    public DefaultViewInjector text(int id, CharSequence charSequence) {
        TextView view = findViewById(id);
        view.setText(charSequence);
        return this;
    }

    @Override
    public DefaultViewInjector hint(int id, CharSequence charSequence) {
        TextView view = findViewById(id);
        view.setHint(charSequence);
        return this;
    }


    @Override
    public DefaultViewInjector textColor(int id, int color) {
        TextView view = findViewById(id);
        view.setTextColor(color);
        return this;
    }

    @Override
    public DefaultViewInjector textSize(int id, int sp) {
        TextView view = findViewById(id);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        return this;
    }


    @Override
    public DefaultViewInjector image(int id, int res) {
        ImageView view = findViewById(id);
        view.setImageResource(res);
        return this;
    }

    @Override
    public DefaultViewInjector image(int id, Drawable drawable) {
        ImageView view = findViewById(id);
        view.setImageDrawable(drawable);
        return this;
    }

    @Override
    public DefaultViewInjector background(int id, int res) {
        View view = findViewById(id);
        view.setBackgroundResource(res);
        return this;
    }

    @SuppressWarnings("deprecation")
    @Override
    public DefaultViewInjector background(int id, Drawable drawable) {
        View view = findViewById(id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
        return this;
    }

    @Override
    public DefaultViewInjector visible(int id) {
        findViewById(id).setVisibility(View.VISIBLE);
        return this;
    }

    @Override
    public DefaultViewInjector gone(int id) {
        findViewById(id).setVisibility(View.GONE);
        return this;
    }

    @Override
    public DefaultViewInjector visibility(int id, int visibility) {
        findViewById(id).setVisibility(visibility);
        return this;
    }

    @Override
    public <V extends View> DefaultViewInjector with(int id, Action<V> action) {
        action.action((V) findViewById(id));
        return this;
    }

    @Override
    public DefaultViewInjector clicked(int id, View.OnClickListener listener) {
        findViewById(id).setOnClickListener(listener);
        return this;
    }

    @Override
    public DefaultViewInjector longClicked(int id, View.OnLongClickListener listener) {
        findViewById(id).setOnLongClickListener(listener);
        return this;
    }

    @Override
    public DefaultViewInjector enable(int id, boolean enable) {
        findViewById(id).setEnabled(enable);
        return this;
    }


    @Override
    public DefaultViewInjector checked(int id, boolean checked) {
        Checkable view = findViewById(id);
        view.setChecked(checked);
        return this;
    }


    @Override
    public DefaultViewInjector addView(int id, View... views) {
        ViewGroup viewGroup = findViewById(id);
        for (View view : views) {
            viewGroup.addView(view);
        }
        return this;
    }


    @Override
    public DefaultViewInjector removeAllViews(int id) {
        ViewGroup viewGroup = findViewById(id);
        viewGroup.removeAllViews();
        return this;
    }

    @Override
    public DefaultViewInjector removeView(int id, View view) {
        ViewGroup viewGroup = findViewById(id);
        viewGroup.removeView(view);
        return this;
    }

}
