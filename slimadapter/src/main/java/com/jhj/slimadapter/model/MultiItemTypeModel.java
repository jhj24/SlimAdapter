package com.jhj.slimadapter.model;

import com.jhj.slimadapter.adapter.SlimAdapter;

import java.util.Map;

/**
 * 当同一种数据类型显示不同的布局时，实体类继承该接口，且方法的返回值必须与
 * {@link SlimAdapter#register(Map, SlimAdapter.ItemViewCallback)}中Map参数的Key对应，Value对应其布局
 * <p>
 * Created by jhj on 18-10-12.
 */

public interface MultiItemTypeModel {

    int getItemType();

}
