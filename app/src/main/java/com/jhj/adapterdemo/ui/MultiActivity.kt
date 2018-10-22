package com.jhj.adapterdemo.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.jhj.adapterdemo.R
import com.jhj.adapterdemo.bean.MultiBean
import com.jhj.slimadapter.adapter.SlimAdapter
import kotlinx.android.synthetic.main.activity_recyclerview.*

/**
 * Created by jhj on 18-10-22.
 */
class MultiActivity : AppCompatActivity() {

    val dataList = arrayListOf<MultiBean>(MultiBean(0), MultiBean(1), MultiBean(2), MultiBean(3),
            MultiBean(4), MultiBean(5), MultiBean(6), MultiBean(7), MultiBean(8), MultiBean(9))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)

        SlimAdapter.creator(LinearLayoutManager(this))
                .register<MultiBean>(1, R.layout.list_item_string) { injector, bean, position ->
                    injector.text(R.id.textView, bean.num.toString())

                }
                .register<MultiBean>(0, R.layout.list_item_int) { injector, bean, position ->
                    injector.text(R.id.textView, bean.num.toString())

                }
                .attachTo(recyclerView)
                .updateData(dataList)
    }
}