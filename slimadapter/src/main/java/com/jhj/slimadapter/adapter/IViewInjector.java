package com.jhj.adapterdemo.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by jhj on 18-10-6.
 */
public interface IViewInjector<VI extends IViewInjector> {

    interface Action<V extends View> {
        void action(V view);
    }

    <T extends View> T findViewById(int id);

    <V extends View> VI with(int id, Action<V> action);

    VI tag(int id, Object object);

    VI text(int id, CharSequence charSequence);

    VI hint(int id, CharSequence charSequence);

    VI textColor(int id, int color);

    VI textSize(int id, int sp);

    VI image(int id, int res);

    VI image(int id, Drawable drawable);

    VI background(int id, int res);

    VI background(int id, Drawable drawable);

    VI visible(int id);

    VI gone(int id);

    VI visibility(int id, int visibility);

    VI clicked(int id, View.OnClickListener listener);

    VI longClicked(int id, View.OnLongClickListener listener);

    VI enable(int id, boolean enable);

    VI checked(int id, boolean checked);

    VI addView(int id, View... views);

    VI removeAllViews(int id);

    VI removeView(int id, View view);
}
