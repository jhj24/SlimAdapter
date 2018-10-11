package com.jhj.slimadapter.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by jhj on 18-10-11.
 */

public interface OnItemClickListener {

    void onItemClicked(RecyclerView recyclerView, View view, int position);

}
