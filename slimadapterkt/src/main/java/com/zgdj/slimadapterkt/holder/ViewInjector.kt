package com.zgdj.slimadapterkt.holder

import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.ImageView
import android.widget.TextView

/**
 * View的辅助方法
 * Created by jhj on 18-10-13.
 */

class ViewInjector(private val holder: SlimViewHolder) {


    fun <V : View> getView(id: Int): V {
        return holder.getView(id)
    }

    fun <V : View> with(id: Int, action: ViewAction<V>): ViewInjector {
        action.onAction(getView<View>(id) as V)
        return this
    }

    fun tag(id: Int, `object`: Any): ViewInjector {
        getView<View>(id).tag = `object`
        return this
    }

    fun text(id: Int, charSequence: CharSequence): ViewInjector {
        val view = getView<TextView>(id)
        view.text = charSequence
        return this
    }

    fun hint(id: Int, charSequence: CharSequence): ViewInjector {
        val view = getView<TextView>(id)
        view.hint = charSequence
        return this
    }


    fun textColor(id: Int, color: Int): ViewInjector {
        val view = getView<TextView>(id)
        view.setTextColor(color)
        return this
    }

    fun textSize(id: Int, sp: Int): ViewInjector {
        val view = getView<TextView>(id)
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp.toFloat())
        return this
    }


    fun image(id: Int, res: Int): ViewInjector {
        val view = getView<ImageView>(id)
        view.setImageResource(res)
        return this
    }

    fun image(id: Int, drawable: Drawable): ViewInjector {
        val view = getView<ImageView>(id)
        view.setImageDrawable(drawable)
        return this
    }

    fun background(id: Int, res: Int): ViewInjector {
        val view = getView<View>(id)
        view.setBackgroundResource(res)
        return this
    }

    fun background(id: Int, drawable: Drawable): ViewInjector {
        val view = getView<View>(id)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.background = drawable
        } else {
            view.setBackgroundDrawable(drawable)
        }
        return this
    }

    fun visible(id: Int): ViewInjector {
        getView<View>(id).visibility = View.VISIBLE
        return this
    }

    fun gone(id: Int): ViewInjector {
        getView<View>(id).visibility = View.GONE
        return this
    }

    fun visibility(id: Int, visibility: Int): ViewInjector {
        getView<View>(id).visibility = visibility
        return this
    }


    fun clicked(id: Int, listener: View.OnClickListener): ViewInjector {
        getView<View>(id).setOnClickListener(listener)
        return this
    }

    fun longClicked(id: Int, listener: View.OnLongClickListener): ViewInjector {
        getView<View>(id).setOnLongClickListener(listener)
        return this
    }

    fun enable(id: Int, enable: Boolean): ViewInjector {
        getView<View>(id).isEnabled = enable
        return this
    }




    fun addView(id: Int, vararg views: View): ViewInjector {
        val viewGroup = getView<ViewGroup>(id)
        for (view in views) {
            viewGroup.addView(view)
        }
        return this
    }


    fun removeAllViews(id: Int): ViewInjector {
        val viewGroup = getView<ViewGroup>(id)
        viewGroup.removeAllViews()
        return this
    }

    fun removeView(id: Int, view: View): ViewInjector {
        val viewGroup = getView<ViewGroup>(id)
        viewGroup.removeView(view)
        return this
    }


    interface ViewAction<V : View> {

        fun onAction(view: V)

    }
}
