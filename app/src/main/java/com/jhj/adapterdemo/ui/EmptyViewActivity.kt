package com.jhj.adapterdemo.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import com.jhj.adapterdemo.R
import com.jhj.slimadapter.SlimAdapter
import com.jhj.slimadapter.callback.ItemViewBind
import com.jhj.slimadapter.holder.ViewInjector
import com.jhj.slimadapter.itemdecoration.GridItemDecoration
import kotlinx.android.synthetic.main.activity_recyclerview.*
import kotlinx.android.synthetic.main.layout_empty_view.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * Created by jhj on 18-10-22.
 */
class EmptyViewActivity : AppCompatActivity() {

    val dataList = arrayListOf<String>("刘德华", "周杰伦", "成龙", "李连杰", "周星驰", "周润华", "吴京", "黄渤", "王宝强", "徐峥")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        val view = LayoutInflater.from(this).inflate(R.layout.layout_empty_view, null, false);
        val adapter = SlimAdapter.creator(GridLayoutManager(this, 3))
                .register<String>(R.layout.list_item_white, object : ItemViewBind<String>() {
                    override fun convert(injector: ViewInjector, bean: String?, position: Int) {
                        injector.text(R.id.textView, bean)
                    }
                })
                .attachTo(recyclerView)
                .addItemDecoration(GridItemDecoration())
                .setEmptyView(this, R.layout.layout_empty_view) { adapter, v ->
                    v.tv_load.onClick {
                        adapter.setDataList(dataList)
                    }
                }
                .setOnLoadMoreListener {
                    it.loadMoreEnd()
                }

    }
}