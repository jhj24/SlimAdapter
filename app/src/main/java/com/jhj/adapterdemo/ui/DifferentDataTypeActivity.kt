package com.jhj.adapterdemo.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.jhj.adapterdemo.R
import com.jhj.slimadapter.SlimAdapter
import com.jhj.slimadapter.callback.ItemViewBind
import com.jhj.slimadapter.holder.ViewInjector
import com.jhj.slimadapter.itemdecoration.LineItemDecoration
import kotlinx.android.synthetic.main.activity_recyclerview.*

/**
 * Created by jhj on 18-10-22.
 */
class DifferentDataTypeActivity : AppCompatActivity() {

    val dataList = arrayListOf("刘德华", 1, "周杰伦", 2, "成龙", 3, "李连杰", 4, "周星驰", "周润华", "吴京", "黄渤", "王宝强", "徐峥")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        SlimAdapter.creator()
                .register<String>(R.layout.list_item_white) {adapter, injector, bean, position ->
                    injector.text(R.id.textView, "这是String类->$bean")
                }
                .register<Int>(R.layout.list_item_putple) { adapter,injector, bean, position ->
                    injector.text(R.id.textView, "这是Int类->$bean")
                }
                .attachTo(recyclerView)
                .addItemDecoration(LineItemDecoration().setDividerColor(0xffff0000.toInt()))
                .setDataList(dataList)
    }
}