package com.zgdj.slimadapterkt.holder

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * SlimViewHolder
 *
 *
 * Created by jhj on 18-10-6.
 */

class SlimViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val viewInjector: ViewInjector
    private val viewMap: SparseArray<View>


    init {
        this.viewMap = SparseArray()
        viewInjector = ViewInjector(this)
    }

    constructor(parent: ViewGroup, layoutRes: Int) : this(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)) {}

    /**
     * 通过viewId获取控件
     *
     * @param id  ViewId
     * @param <V> <V extends View>
     * @return View
     */
    @Suppress("UNCHECKED_CAST")
    fun <V : View> getView(id: Int): V {
        var view: View? = viewMap.get(id)
        if (view == null) {
            view = itemView.findViewById(id)
            viewMap.put(id, view)
        }
        return view as V
    }
}
