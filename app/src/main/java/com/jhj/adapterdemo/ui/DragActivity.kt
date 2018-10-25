package com.jhj.adapterdemo.ui

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.TextView
import com.jhj.adapterdemo.R
import com.jhj.slimadapter.adapter.DraggableAdapter
import kotlinx.android.synthetic.main.activity_recyclerview.*
import org.jetbrains.anko.toast

/**
 * 拖拽、滑动删除
 * Created by jhj on 18-10-23.
 */
class DragActivity : AppCompatActivity() {

    val dataList = arrayListOf<String>("刘德华", "周杰伦", "成龙", "李连杰", "周星驰", "周润华", "吴京", "黄渤", "王宝强", "徐峥")
    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)

        val textView = TextView(this)
        textView.text = "222"

        val flag = ItemTouchHelper.UP or ItemTouchHelper.DOWN;
        DraggableAdapter.creator(LinearLayoutManager(this))
                .register<String>(R.layout.list_item_string) { injector, bean, position ->
                    injector.text(R.id.textView, bean.toString())


                }
                .setOnItemClickListener { recyclerView, view, position ->
                    toast(position.toString() + "——>" + dataList[position])
                }
                .setOnItemLongClickListener { recyclerView, view, position ->
                    toast(".....")
                    true
                }
                .attachTo(recyclerView)
                .updateData(dataList)



    }
}