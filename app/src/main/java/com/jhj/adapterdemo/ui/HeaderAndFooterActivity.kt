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
import kotlinx.android.synthetic.main.list_item_white.view.*

/**
 * Created by jhj on 18-10-22.
 */
class HeaderAndFooterActivity : AppCompatActivity() {

    val dataList = arrayListOf("刘德华", "周杰伦", "成龙", "李连杰", "周星驰", "周润华", "吴京", "黄渤", "王宝强", "徐峥")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)


        val adapter = SlimAdapter.creator(LinearLayoutManager(this))
                .register<String>(R.layout.list_item_white) { injector, bean, position ->
                    injector.text(R.id.textView, bean)
                }
                .attachTo(recyclerView)
                .addItemDecoration(LineItemDecoration())
                .setDataList(dataList)
                .addHeader(this, R.layout.list_item_putple) { a, it ->
                    it.textView.text = "这是一个标题"
                }
                .addFooter(this, R.layout.list_item_putple) { a, it ->

                    it.textView.text = "这是一个结尾"
                }


    }




}