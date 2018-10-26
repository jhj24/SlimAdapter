package com.jhj.adapterdemo.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.jhj.adapterdemo.R
import com.jhj.slimadapter.SlimAdapter
import kotlinx.android.synthetic.main.activity_recyclerview.*
import kotlinx.android.synthetic.main.list_item_string.view.*

/**
 * Created by jhj on 18-10-22.
 */
class HeaderAndFooterActivity : AppCompatActivity() {

    val dataList = arrayListOf<String>("刘德华", "周杰伦", "成龙", "李连杰", "周星驰", "周润华", "吴京", "黄渤", "王宝强", "徐峥")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)


        SlimAdapter.creator(LinearLayoutManager(this))
                .register<String>(R.layout.list_item_string) { injector, bean, position ->
                    injector.text(R.id.textView, bean)

                }

                .attachTo(recyclerView)
                .addHeader(this, R.layout.list_item_int) {adapter,it->
                    it.textView.text = "这是一个标题"
                }
                .addFooter(this, R.layout.list_item_int) {adapter,it->
                    it.textView.text = "这是一个结尾"
                }
                .updateData(dataList)

    }


}