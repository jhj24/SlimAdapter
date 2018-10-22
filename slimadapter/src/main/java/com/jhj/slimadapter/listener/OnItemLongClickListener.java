package com.jhj.slimadapter.listener;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 长按事件
 * Created by jhj on 18-10-11.
 */

public interface OnItemLongClickListener {

    boolean onItemLongClicked(RecyclerView recyclerView, View view, int position);

}