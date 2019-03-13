package com.zgdj.slimadapterkt.model


import com.zgdj.slimadapterkt.SlimAdapter

/**
 * 当同一种数据类型显示不同的布局时，实体类继承该接口，且[MultiItemTypeModel.getItemType]方法的返回值必须与
 * [SlimAdapter.register]方法的第一个参数对应
 *
 *
 * Created by jhj on 18-10-12.
 */

interface MultiItemTypeModel {

    val itemType: Int

}
