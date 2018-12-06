package com.jhj.adapterdemo.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.jhj.adapterdemo.R
import com.jhj.adapterdemo.bean.ApplyBean
import com.jhj.adapterdemo.net.DataResult
import com.jhj.adapterdemo.net.HttpConfig
import com.jhj.httplibrary.HttpCall
import com.jhj.httplibrary.callback.base.BaseHttpCallback
import com.jhj.slimadapter.SlimAdapter
import com.jhj.slimadapter.callback.ItemViewBind
import com.jhj.slimadapter.holder.ViewInjector
import com.jhj.slimadapter.itemdecoration.LineItemDecoration
import kotlinx.android.synthetic.main.activity_recyclerview.*

/**
 * Created by jhj on 18-10-22.
 */
class LoadMoreActivity : AppCompatActivity() {

    val pageSize = 14;
    var pageNo = 0;
    var isHasData = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)


        val adapter = SlimAdapter.creator(LinearLayoutManager(this))
                .register<ApplyBean>(R.layout.list_item_white, object : ItemViewBind<ApplyBean>() {
                    override fun convert(injector: ViewInjector, bean: ApplyBean?, position: Int) {
                        injector.text(R.id.textView, bean?.leaveTypeName)
                    }
                })
                .attachTo(recyclerView)
                .addItemDecoration(LineItemDecoration())
                .setOnLoadMoreListener {
                    if (isHasData) {
                        setData(it, 0)
                    } else {
                        it.loadMoreEnd()
                    }
                }
        setData(adapter, 1)
    }

    private fun setData(adapter: SlimAdapter, i: Int) {
        HttpCall.post(HttpConfig.a)
                .addParam("memberId", "754")
                .addParam("pageSize", pageSize.toString())
                .addParam("pageNo", pageNo.toString())
                .enqueue(object : BaseHttpCallback<DataResult<List<ApplyBean>>>() {
                    override fun onFailure(msg: String, errorCode: Int) {
                        adapter.loadMoreFail()
                    }

                    override fun onSuccess(data: DataResult<List<ApplyBean>>?, resultType: ResultType) {
                        val list = data?.data
                        isHasData = list?.size ?: 0 >= pageSize
                        pageNo++
                        if (i == 0) {
                            adapter.addDataList(list)
                        } else {
                            adapter.setDataList(list)
                        }
                    }
                })
    }
}