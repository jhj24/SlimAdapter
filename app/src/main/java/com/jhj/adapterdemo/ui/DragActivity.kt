package com.jhj.adapterdemo.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.jhj.adapterdemo.R
import com.jhj.slimadapter.DraggableAdapter
import com.jhj.slimadapter.itemdecoration.LineItemDecoration
import com.jhj.slimadapter.listener.OnItemDragListener
import kotlinx.android.synthetic.main.activity_recyclerview.*

/**
 * 拖拽、滑动删除
 * Created by jhj on 18-10-23.
 */
class DragActivity : AppCompatActivity() {

    val dataList = arrayListOf<String>("刘德华", "周杰伦", "成龙", "李连杰", "周星驰", "周润华", "吴京", "黄渤", "王宝强", "徐峥")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)

        val textView = TextView(this)
        textView.text = "222"

        DraggableAdapter.creator(LinearLayoutManager(this))
                .register<String>(R.layout.list_item_putple) { injector, bean, position ->
                    injector.text(R.id.textView, bean.toString())
                            .clicked(View.OnClickListener {

                            })
                }
                .attachTo(recyclerView)
                .addItemDecoration(LineItemDecoration())
                .setItemTouchHelper()
                .setDragItem(true)
                .setOnItemDragListener(object : OnItemDragListener {
                    override fun onItemDragMoving(source: RecyclerView.ViewHolder?, from: Int, target: RecyclerView.ViewHolder?, to: Int) {

                    }

                    override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                        viewHolder?.itemView?.scaleX = 0.9f
                        viewHolder?.itemView?.scaleY = 0.9f
                    }

                    override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                        viewHolder?.itemView?.scaleX = 1f
                        viewHolder?.itemView?.scaleY = 1f
                    }
                })
                .setSwipeItem(true)
                .setSwipeFadeOutAnim(true)
                .setDataList(dataList)


    }
}