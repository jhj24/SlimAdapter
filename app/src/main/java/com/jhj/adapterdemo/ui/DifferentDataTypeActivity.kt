package com.jhj.adapterdemo.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.jhj.adapterdemo.R
import com.jhj.slimadapter.adapter.SlimAdapter
import kotlinx.android.synthetic.main.activity_recyclerview.*

/**
 * Created by jhj on 18-10-22.
 */
class DifferentDataTypeActivity : AppCompatActivity() {

    val dataList = arrayListOf("刘德华", 1, "周杰伦", 2, "成龙", 3, "李连杰", 4, "周星驰", "周润华", "吴京", "黄渤", "王宝强", "徐峥")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)

        SlimAdapter.creator(LinearLayoutManager(this))
                .register<String>(R.layout.list_item_string) { injector, bean, position ->
                    injector.text(R.id.textView, bean)

                }
                .register<Int>(R.layout.list_item_int) { injector, bean, position ->
                    injector.text(R.id.textView, bean.toString())
                }
                .attachTo(recyclerView)
                .updateData(dataList)
    }
}