package com.jhj.adapterdemo.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import com.jhj.adapterdemo.R
import com.jhj.slimadapter.adapter.SlimAdapter
import kotlinx.android.synthetic.main.activity_recyclerview.*
import kotlinx.android.synthetic.main.layout_empty_view.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by jhj on 18-10-22.
 */
class EmptyViewActivity : AppCompatActivity() {

    val dataList = arrayListOf<String>("刘德华", "周杰伦", "成龙", "李连杰", "周星驰", "周润华", "吴京", "黄渤", "王宝强", "徐峥")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        val view = LayoutInflater.from(this).inflate(R.layout.layout_empty_view, null, false);
        val adapter = SlimAdapter.creator(LinearLayoutManager(this))
                .register<String>(R.layout.list_item_string) { injector, bean, position ->
                    injector.text(R.id.textView, bean)

                }
                .attachTo(recyclerView)
                .setEmptyView(this, R.layout.layout_empty_view) { adapter, v ->
                    v.tv_load.onClick {
                        adapter.updateData(dataList)
                    }
                }
                .setOnLoadMoreListener {
                    it.loadMoreEnd()
                }

    }
}