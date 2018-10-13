package com.jhj.adapterdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import android.widget.Toast
import com.jhj.slimadapter.adapter.ItemViewCallback
import com.jhj.slimadapter.adapter.SlimAdapter
import com.jhj.slimadapter.holder.ViewInjector
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item_int.view.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gridLayoutManager = GridLayoutManager(this, 2)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                //可以使用getItem(position)获取指定位置数据，根据里面的参数等方式的然后设置显示样式
                return if (position == 2 || position == 3) {
                    2
                } else if (position == 4) {
                    2
                } else {
                    2
                }
            }
        }

        val textView = TextView(this)
        textView.setText("这个一个表头")

        val textView1 = TextView(this)
        textView1.setText("这个一个表尾")
        SlimAdapter.creator()
                /*.register<String>(R.layout.list_item_string) { data, injector, pos ->
                    injector?.text(R.id.textView, data)
                }*/
                .register<String>(R.layout.list_item_string) { holder, t, position ->
                    holder?.text(R.id.textView, t)
                }
                .register<MultiBean>(mapOf(0 to R.layout.list_item_int, 1 to R.layout.list_item_string), object : ItemViewCallback<MultiBean> {
                    override fun convert(holder: ViewInjector?, t: MultiBean?, position: Int) {
                        holder?.text(R.id.textView, t?.a.toString())
                    }

                })
                .attachTo(recyclerView)
                /*.addHeader(this, R.layout.list_item_string) {
                    it.textView.text = "这是一个标题1"
                }
                .addHeader(this, R.layout.list_item_string) {
                    it.textView.text = "这是一个标题2"
                }
                .addHeader(this, R.layout.list_item_string) {
                    it.textView.text = "这是一个标题3"
                }
                .addFooter(textView1)
                .addFooter(this, R.layout.list_item_string) {
                    it.textView.text = "这是一个表尾１"
                }
                .addFooter(this, R.layout.list_item_string) {
                    it.textView.text = "这是一个表尾２"
                }*/
                .addItemDecoration(com.jhj.adapterdemo.DividerItemDecoration(this))
                .setOnItemClickListener { recyclerView, view, position ->
                    Toast.makeText(this, "点击" + position, Toast.LENGTH_SHORT).show()
                }
                .setOnItemLongClickListener { recyclerView, view, position ->
                    Toast.makeText(this, "长按" + position, Toast.LENGTH_SHORT).show()
                    false
                }
                 .layoutManager(gridLayoutManager)
                //.layoutManager(LinearLayoutManager(this))
                .updateData(arrayListOf(MultiBean(1), MultiBean(2), MultiBean(3), MultiBean(4), MultiBean(5),
                        MultiBean(6), MultiBean(7), MultiBean(8), MultiBean(9), MultiBean(10),
                        MultiBean(11), MultiBean(12), MultiBean(13), MultiBean(14), MultiBean(15),
                        MultiBean(16), MultiBean(17), MultiBean(18), MultiBean(19), MultiBean(20),
                        MultiBean(21), MultiBean(22), MultiBean(23), MultiBean(24), MultiBean(25),"1","2","3","4","5"))

        // .removeHeader(1)


    }
}
