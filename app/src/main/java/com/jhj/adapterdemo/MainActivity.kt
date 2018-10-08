package com.jhj.adapterdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.jhj.slimadapter.adapter.SlimAdapter
import com.jhj.slimadapter.adapter.SlimInjector
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = SlimAdapter.creater()
                .register<String>(R.layout.list_item_string, SlimInjector<String> { data, injector ->
                    injector?.text(R.id.textView, data)
                })
                .register<Int>(R.layout.list_item_string, SlimInjector<Int> { data, injector ->
                    injector?.text(R.id.textView,data.toString())
                })
                .attachTo(recyclerView)
                .updateData(arrayListOf("1", "2", "3",1,2));

        recyclerView.adapter = adapter
    }
}
