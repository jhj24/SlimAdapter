package com.jhj.adapterdemo.bean;

import com.jhj.slimadapter.model.MultiItemTypeModel;

/**
 * Created by jhj on 18-10-11.
 */

public class MultiBean implements MultiItemTypeModel {

    public int num;

    public MultiBean(int num) {
        this.num = num;
    }

    @Override
    public int getItemType() {
        if (num % 2 == 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
