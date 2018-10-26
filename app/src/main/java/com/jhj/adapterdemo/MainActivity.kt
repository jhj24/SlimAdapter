package com.jhj.adapterdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.jhj.adapterdemo.ui.*
import com.jhj.slimadapter.adapter.SlimAdapter
import kotlinx.android.synthetic.main.activity_recyclerview.*
import org.jetbrains.anko.startActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)

        val list = arrayListOf<String>("普通布局", "不同数据类型布局", "同数据类型不同显示样式", "带标题和结尾布局", "加载更多", "没有数据的布局", "拖拽和滑动删除", "滑动菜单")

        SlimAdapter.creator(LinearLayoutManager(this))
                .register<String>(R.layout.list_item_string) { injector, bean, position ->
                    injector.text(R.id.textView, bean)
                }
                .attachTo(recyclerView)
                .updateData(list)
                .setOnItemClickListener { recyclerView, view, position ->
                    when (position) {
                        0 -> startActivity<CommonActivity>()
                        1 -> startActivity<DifferentDataTypeActivity>()
                        2 -> startActivity<MultiActivity>()
                        3 -> startActivity<HeaderAndFooterActivity>()
                        4 -> startActivity<LoadMoreActivity>()
                        5 -> startActivity<EmptyViewActivity>()
                        6 -> startActivity<DragActivity>()
                        7 -> startActivity<SwipeMenuActivity>()
                    }
                }



    }
}
