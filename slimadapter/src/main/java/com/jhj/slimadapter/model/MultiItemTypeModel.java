package com.jhj.slimadapter.model;

import com.jhj.slimadapter.callback.ItemViewBind;
import com.jhj.slimadapter.SlimAdapter;

/**
 * 当同一种数据类型显示不同的布局时，实体类继承该接口，且{@link MultiItemTypeModel#getItemType()}方法的返回值必须与
 * {@link SlimAdapter#register(int, int, ItemViewBind)}方法的第一个参数对应
 * <p>
 * Created by jhj on 18-10-12.
 */

public interface MultiItemTypeModel {

    int getItemType();

}
