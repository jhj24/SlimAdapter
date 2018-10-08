package com.jhj.adapterdemo;

import android.support.annotation.Nullable;

import com.jhj.slimadapter.adapter.IViewInjector;
import com.jhj.slimadapter.adapter.SlimAdapter;
import com.jhj.slimadapter.adapter.SlimInjector;


/**
 * Created by jhj on 18-10-8.
 */

public class Test {
    public void a() {
        SlimAdapter.creater().register(R.layout.activity_main, new SlimInjector<String>() {
            @Override
            public void onInject(@Nullable String data, @Nullable IViewInjector injector) {

            }
        });
    }
}
