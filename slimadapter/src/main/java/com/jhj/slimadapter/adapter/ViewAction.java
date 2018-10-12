package com.jhj.slimadapter.adapter;

import android.view.View;

/**
 * Created by jhj on 18-10-12.
 */

public interface ViewAction<V extends View> {

   void onAction(V view);
}
