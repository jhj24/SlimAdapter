package com.jhj.adapterdemo;

import com.jhj.slimadapter.model.MultiItemTypeModel;

/**
 * Created by jhj on 18-10-11.
 */

public class MultiBean implements MultiItemTypeModel {

    public int a;

    public MultiBean(int a) {
        this.a = a;
    }

    @Override
    public int getItemType() {
        if (a % 2 == 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
