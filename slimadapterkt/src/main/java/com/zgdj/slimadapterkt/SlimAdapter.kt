package com.zgdj.slimadapterkt

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.zgdj.slimadapterkt.callback.ItemViewBind
import com.zgdj.slimadapterkt.callback.ItemViewDelegate
import com.zgdj.slimadapterkt.holder.SlimViewHolder
import com.zgdj.slimadapterkt.holder.ViewInjector
import com.zgdj.slimadapterkt.itemtouch.ItemTouchHelperCallback
import com.zgdj.slimadapterkt.listener.OnItemDragListener
import com.zgdj.slimadapterkt.listener.OnItemSwipeListener
import com.zgdj.slimadapterkt.model.MultiItemTypeModel
import com.zgdj.slimadapterkt.more.LoadMoreView
import com.zgdj.slimadapterkt.more.SimpleLoadMoreView
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

/**
 * Created by jhj on 18-10-25.
 */

class SlimAdapter private constructor(manager: RecyclerView.LayoutManager) : RecyclerView.Adapter<SlimViewHolder>() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var genericActualType: Type? = null
    private val dataViewTypeList = ArrayList<Type>()
    private val multiViewTypeList = ArrayList<Int>()
    private val itemViewMap = HashMap<Type, ItemViewDelegate<*>>()
    private val multiViewMap = SparseArray<ItemViewDelegate<*>>()

    private var dataList: MutableList<*>? = null
    private var recyclerView: RecyclerView? = null


    private val headerItemViewList = ArrayList<View>()
    private val footerItemViewList = ArrayList<View>()
    private var loadMoreView: LoadMoreView = SimpleLoadMoreView()
    private var emptyItemView: View? = null

    private var blockLoadMore: ((SlimAdapter) -> Unit)? = null
    private var isHeaderWholeLine = true
    private var isFooterWholeLine = true

    //滑动拖拽
    private var isLongPressDragEnable = false
    private var isItemSwipeEnable = false
    private var itemTouchHelperCallback: ItemTouchHelperCallback? = null

    init {
        this.layoutManager = manager
    }


    val headerViewCount: Int
        get() = headerItemViewList.size

    private val loadMoreViewPosition: Int
        get() = headerItemViewList.size + dataListCount + footerItemViewList.size

    private val dataListCount: Int
        get() = dataList?.size ?: 0

    fun getDataList(): List<*> {
        return dataList ?: throw NullPointerException("DataList is null,Please use setDataList()")
    }

    fun getRecyclerView(): RecyclerView {
        return recyclerView
                ?: throw NullPointerException("RecyclerView is null,Please first use attachTo(recyclerView) method")
    }

    fun <D> register(@LayoutRes layoutRes: Int, bind: ItemViewBind<D>): SlimAdapter {
        val type: Type = (if (getGenericActualType() == null) {
            getDataActualType(bind)
        } else {
            getGenericActualType()
        }) ?: throw IllegalArgumentException()

        if (dataViewTypeList.contains(type)) {
            throw IllegalArgumentException("The same data type can only use the register() method once.")
        }
        dataViewTypeList.add(type)
        itemViewMap[type] = object : ItemViewDelegate<D> {

            override val itemViewLayoutId: Int
                get() = layoutRes

            override fun injector(injector: ViewInjector, data: D, position: Int) {
                bind.convert(this@SlimAdapter, injector, data, position)
            }
        }
        return this
    }

    fun <D : MultiItemTypeModel> register(viewType: Int, @LayoutRes layoutRes: Int, bind: ItemViewBind<D>): SlimAdapter {
        if (multiViewTypeList.contains(viewType)) {
            throw IllegalArgumentException("please use different viewType")
        }
        multiViewTypeList.add(viewType)
        multiViewMap.put(viewType, object : ItemViewDelegate<D> {
            override val itemViewLayoutId: Int
                get() = layoutRes

            override fun injector(injector: ViewInjector, data: D, position: Int) {
                bind.convert(this@SlimAdapter, injector, data, position)
            }
        })
        return this
    }


    fun attachTo(recyclerView: RecyclerView): SlimAdapter {
        this.recyclerView = recyclerView
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = this
        return this
    }

    fun addItemDecoration(itemDecoration: RecyclerView.ItemDecoration): SlimAdapter {
        getRecyclerView().addItemDecoration(itemDecoration)
        return this
    }

    fun <D> setDataList(dataList: MutableList<D>): SlimAdapter {
        this.dataList = dataList
        notifyDataSetChanged()
        resetLoadMoreStates()
        return this
    }

    fun <D> addDataList(dataList: List<D>): SlimAdapter {
        val startIndex = getDataList().size + headerViewCount
        getDataList().toArrayList().addAll(dataList)
        notifyItemRangeInserted(startIndex, dataList.size)
        resetLoadMoreStates()
        return this
    }

    fun <D> addDataList(index: Int, dataList: List<D>): SlimAdapter {
        getDataList().toArrayList().addAll(index, dataList)
        notifyItemRangeInserted(index + headerViewCount, dataList.size)
        resetLoadMoreStates()
        return this
    }

    fun <D> addData(data: D): SlimAdapter {
        val startIndex = getDataList().size + headerViewCount
        getDataList().toArrayList().add(data)
        notifyItemInserted(startIndex)
        resetLoadMoreStates()
        return this
    }

    fun <D> addData(index: Int, data: D): SlimAdapter {
        getDataList().toArrayList().add(index, data)
        notifyItemInserted(index + headerViewCount)
        resetLoadMoreStates()
        return this
    }


    fun remove(index: Int): SlimAdapter {
        getDataList().toArrayList().removeAt(index)
        notifyItemChanged(index + headerItemViewList.size)
        return this
    }


    // ======================================= header ================================

    fun addHeader(context: Context, layoutRes: Int, block: (SlimAdapter, View) -> Unit): SlimAdapter {
        val view = LayoutInflater.from(context).inflate(layoutRes, null, false)
        block(this, view)
        return addHeader(view)
    }

    fun addHeader(context: Context, layoutRes: Int): SlimAdapter {
        return addHeader(LayoutInflater.from(context).inflate(layoutRes, null, false))
    }

    fun addHeader(view: View): SlimAdapter {
        if (layoutManager is LinearLayoutManager) {
            view.layoutParams = if ((layoutManager as LinearLayoutManager).orientation == LinearLayout.VERTICAL) {
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            } else {
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        }
        headerItemViewList.add(view)
        notifyDataSetChanged()
        return this
    }

    fun removeHeader(index: Int): SlimAdapter {
        this.headerItemViewList.removeAt(index)
        notifyItemChanged(index)
        return this
    }

    fun setHeaderWholeLine(headerWholeLine: Boolean): SlimAdapter {
        this.isHeaderWholeLine = headerWholeLine
        return this
    }

    //====================================== footer ==============================
    fun addFooter(context: Context, layoutRes: Int, block: (SlimAdapter, View) -> Unit): SlimAdapter {
        val view = LayoutInflater.from(context).inflate(layoutRes, null, false)
        block(this, view)
        return addFooter(view)
    }


    fun addFooter(context: Context, layoutRes: Int): SlimAdapter {
        return addFooter(LayoutInflater.from(context).inflate(layoutRes, null, false))
    }

    fun addFooter(view: View): SlimAdapter {
        if (layoutManager is LinearLayoutManager) {
            view.layoutParams = if ((layoutManager as LinearLayoutManager).orientation == LinearLayout.VERTICAL) {
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            } else {
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        }
        footerItemViewList.add(view)
        notifyDataSetChanged()
        return this
    }

    fun removeFooter(index: Int): SlimAdapter {
        this.headerItemViewList.removeAt(index)
        notifyItemChanged(index + headerItemViewList.size + dataListCount)
        return this
    }

    fun setFooterWholeLine(footerWholeLine: Boolean): SlimAdapter {
        this.isFooterWholeLine = footerWholeLine
        return this
    }

    //============================= load more ===============================

    fun setOnLoadMoreListener(block: (SlimAdapter) -> Unit): SlimAdapter {
        this.blockLoadMore = block
        return this
    }

    fun setLoadMoreView(loadingView: LoadMoreView): SlimAdapter {
        this.loadMoreView = loadingView
        return this
    }

    fun loadMoreEnd() {
        if (blockLoadMore == null || loadMoreViewPosition == 0) {
            return
        }
        loadMoreView.loadMoreStatus = LoadMoreView.STATUS_END
        notifyItemChanged(loadMoreViewPosition)
    }


    fun loadMoreFail() {
        if (blockLoadMore == null || loadMoreViewPosition == 0) {
            return
        }
        loadMoreView.loadMoreStatus = LoadMoreView.STATUS_FAIL
        notifyItemChanged(loadMoreViewPosition)
    }

    fun loadMoreFail(listener: LoadMoreView.OnLoadMoreFailClickClListener) {
        loadMoreFail()
        loadMoreView.setLoadFailOnClickListener(listener)
    }

    private fun resetLoadMoreStates() {
        if (blockLoadMore != null) {
            loadMoreView.loadMoreStatus = LoadMoreView.STATUS_LOADING
            notifyItemChanged(loadMoreViewPosition)
        }
    }


    //==================================empty view ================================
    //设置Empty　View 时，该View只在header 、footer、dataList的大小都是０时显示

    fun setEmptyView(context: Context, layoutRes: Int, block: (SlimAdapter, View) -> Unit): SlimAdapter {
        val view = LayoutInflater.from(context).inflate(layoutRes, null, false)
        block(this, view)
        setEmptyView(view)
        return this
    }

    fun setEmptyView(context: Context, layoutRes: Int): SlimAdapter {
        val view = LayoutInflater.from(context).inflate(layoutRes, null, false)
        setEmptyView(view)
        return this
    }

    fun setEmptyView(emptyView: View): SlimAdapter {
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        emptyView.layoutParams = params
        emptyItemView = emptyView
        notifyDataSetChanged()
        return this
    }

    //==============================滑动拖拽=======================================

    fun setItemTouchHelper(): SlimAdapter {
        itemTouchHelperCallback = ItemTouchHelperCallback(this)
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback!!)
        itemTouchHelper.attachToRecyclerView(getRecyclerView())
        return this
    }

    //======　drag　======


    fun setDragItem(isDrag: Boolean): SlimAdapter {
        this.isLongPressDragEnable = isDrag
        return this
    }

    fun setDragFlags(dragFlags: Int): SlimAdapter {
        itemTouchHelperCallback?.setDragFlags(dragFlags)
        return this
    }

    fun setOnItemDragListener(onItemDragListener: OnItemDragListener): SlimAdapter {
        itemTouchHelperCallback?.setOnItemDragListener(onItemDragListener)
        return this
    }

    fun setMoveThreshold(moveThreshold: Float): SlimAdapter {
        itemTouchHelperCallback?.setMoveThreshold(moveThreshold)
        return this
    }

    //======= swipe =======

    fun getItemSwipeEnable(): Boolean {
        return isItemSwipeEnable
    }

    fun setItemSwipeItem(isSwipe: Boolean): SlimAdapter {
        isItemSwipeEnable = isSwipe
        return this
    }

    fun getLongPressDragEnable(): Boolean {
        return isLongPressDragEnable
    }

    fun setSwipeFlags(swipeFlags: Int): SlimAdapter {
        itemTouchHelperCallback?.setSwipeFlags(swipeFlags)
        return this
    }

    fun setOnItemSwipeListener(listener: OnItemSwipeListener): SlimAdapter {
        itemTouchHelperCallback?.setOnItemSwipeListener(listener)
        return this
    }

    fun setSwipeThreshold(swipeThreshold: Float): SlimAdapter {
        itemTouchHelperCallback?.setSwipeThreshold(swipeThreshold)
        return this
    }

    fun setSwipeFadeOutAnim(swipeFadeOutAnim: Boolean): SlimAdapter {
        itemTouchHelperCallback?.setSwipeFadeOutAnim(swipeFadeOutAnim)
        return this
    }


    private fun getGenericActualType(): Type? {
        return genericActualType
    }

    /**
     * 二次泛型封装会出现错误，可以通过该方法获取实际类型
     *
     * @return 泛型的实际类型
     */
    fun setGenericActualType(genericActualType: Type): SlimAdapter {
        this.genericActualType = genericActualType
        return this
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlimViewHolder {

        when {
            isHeaderView(viewType) -> //header
                return SlimViewHolder(headerItemViewList[HEADER_VIEW_TYPE - viewType])
            isFooterView(viewType) -> //footer
                return SlimViewHolder(footerItemViewList[FOOTER_VIEW_TYPE - viewType])
            isLoadMoreView(viewType) -> {//more
                val view = LayoutInflater.from(parent.context).inflate(loadMoreView.layoutId, parent, false)
                return SlimViewHolder(view)

            }
            isEmptyView(viewType) -> //empty
                return SlimViewHolder(emptyItemView ?: throw NullPointerException())
            isNormalBodyView(viewType) -> { //normal body
                val dataType = dataViewTypeList[BODY_VIEW_TYPE - viewType]
                val itemView = itemViewMap[dataType]
                        ?: throw NullPointerException("missing related layouts corresponding to data types," +
                                "please add related layout:" + dataType)
                val layoutRes = itemView.itemViewLayoutId
                return SlimViewHolder(parent, layoutRes)

            }
            multiViewTypeList.contains(viewType) -> {//multi body
                val itemView = multiViewMap.get(viewType)
                        ?: throw NullPointerException("Because you used a multi-style layout to inherit the com.jhj.slimadapter.model.MutilItemTypeModel" + ", But did not find the layout corresponding to the return value of the getItemType() method.")
                val layoutRes = itemView.itemViewLayoutId
                return SlimViewHolder(parent, layoutRes)

            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: SlimViewHolder, position: Int) {

        val bodyPosition = position - headerItemViewList.size

        if (bodyPosition in 0..(dataListCount - 1)) {//body

            val data = getDataList()[bodyPosition]
            if (data != null) {
                if (data is MultiItemTypeModel) {
                    val itemView = multiViewMap.get(data.itemType) as ItemViewDelegate<Any>?
                    itemView?.injector(holder.viewInjector, data, position)
                } else {
                    val itemView = itemViewMap[data::class.java] as ItemViewDelegate<Any>?
                    itemView?.injector(holder.viewInjector, data, position)
                }
            }
        } else if (isShowLoadMoreView(position)) {

            autoLoadMore()
            loadMoreView.convert(holder)
        }
    }

    override fun getItemViewType(position: Int): Int {

        val bodyPosition = position - headerItemViewList.size
        val footerPosition = position - (dataListCount + headerItemViewList.size)

        return if (position < headerItemViewList.size) { //header
            HEADER_VIEW_TYPE - position

        } else if (bodyPosition in 0..(dataListCount - 1)) { //body
            val item = getDataList()[bodyPosition]
            if (item != null) {
                if (item is MultiItemTypeModel) { //多样式布局
                    item.itemType

                } else {//普通布局
                    val index = dataViewTypeList.indexOf(item::class.java)
                    BODY_VIEW_TYPE - index
                }
            } else {
                super.getItemViewType(position)
            }

        } else if (position >= headerItemViewList.size + dataListCount && position < dataListCount + headerItemViewList.size + footerItemViewList.size) { //footer
            FOOTER_VIEW_TYPE - footerPosition

        } else if (isShowLoadMoreView(position)) { //more
            MORE_VIEW_TYPE

        } else if (emptyItemView != null && dataListCount + headerItemViewList.size + footerItemViewList.size == 0) {  //empty
            EMPTY_VIEW_TYPE

        } else {
            super.getItemViewType(position)
        }
    }

    override fun getItemCount(): Int {
        if (emptyItemView != null && loadMoreViewPosition == 0) {
            return 1
        }
        return if (blockLoadMore != null) {
            if (loadMoreViewPosition == 0) {
                0
            } else {
                dataListCount + headerItemViewList.size + footerItemViewList.size + 1
            }
        } else {
            dataListCount + headerItemViewList.size + footerItemViewList.size
        }
    }

    override fun onViewAttachedToWindow(holder: SlimViewHolder) {
        super.onViewAttachedToWindow(holder)
        val type = holder.itemViewType
        if (isHeaderView(type) || isFooterView(type) || isLoadMoreView(type) || isEmptyView(type)) {
            setFullSpan(holder)
        } else {
            //addAnimation(holder);
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            val spanSizeLookup = layoutManager.spanSizeLookup

            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val viewType = getItemViewType(position)
                    if (isLoadMoreView(viewType)) {
                        return layoutManager.spanCount
                    } else if (isFooterView(viewType) && isFooterWholeLine) {
                        return layoutManager.spanCount
                    } else if (isHeaderView(viewType) && isHeaderWholeLine) {
                        return layoutManager.spanCount
                    } else if (isEmptyView(viewType)) {
                        return layoutManager.spanCount
                    }

                    return spanSizeLookup?.getSpanSize(position) ?: 1
                }
            }
            layoutManager.spanCount = layoutManager.spanCount
        }

    }

    //========= ViewType判断 ========
    fun isHeaderView(viewType: Int): Boolean {
        return viewType in (FOOTER_VIEW_TYPE + 1)..HEADER_VIEW_TYPE
    }

    fun isFooterView(viewType: Int): Boolean {
        return viewType in (MORE_VIEW_TYPE + 1)..FOOTER_VIEW_TYPE
    }

    fun isLoadMoreView(viewType: Int): Boolean {
        return viewType == MORE_VIEW_TYPE
    }

    fun isEmptyView(viewType: Int): Boolean {
        return viewType == EMPTY_VIEW_TYPE
    }

    fun isNormalBodyView(viewType: Int): Boolean {
        return viewType <= BODY_VIEW_TYPE
    }

    //====== 其他 =======


    private fun autoLoadMore() {

        if (loadMoreView.loadMoreStatus != LoadMoreView.STATUS_LOADING) {
            return
        }

        loadMoreView.loadMoreStatus = LoadMoreView.STATUS_LOADING

        if (recyclerView != null) {
            getRecyclerView().post {
                blockLoadMore?.invoke(this@SlimAdapter)
            }
        } else {
            blockLoadMore?.invoke(this@SlimAdapter)
        }

    }


    /**
     * 是否加载更多，当位于０位置的时候，不显示
     *
     * @param position 　position
     * @return boolean
     */
    private fun isShowLoadMoreView(position: Int): Boolean {
        return blockLoadMore != null && position != 0 && position == loadMoreViewPosition
    }

    private fun setFullSpan(holder: RecyclerView.ViewHolder) {
        if (holder.itemView.layoutParams is StaggeredGridLayoutManager.LayoutParams) {
            val params = holder
                    .itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
            params.isFullSpan = true
        }
    }


    private fun <D> getDataActualType(bind: ItemViewBind<D>): Type? {
        val interfaces = bind.javaClass.genericInterfaces
        for (type in interfaces) {
            if (type is ParameterizedType) {
                if (type.rawType == ItemViewBind::class.java) {
                    val actualType = type.actualTypeArguments[0]
                    return actualType as? Class<D>
                            ?: throw IllegalArgumentException("The generic type argument of Slim is NOT support " + "Generic Parameterized Type now, Please getGenericActualType() method return Actual type.")
                }
            }
        }
        return null
    }

    private fun <T> List<T>?.toArrayList(): ArrayList<T> {
        return ArrayList(this.orEmpty())
    }

    companion object {

        private const val HEADER_VIEW_TYPE = -0x00100000
        private const val FOOTER_VIEW_TYPE = -0x00200000
        private const val MORE_VIEW_TYPE = -0x00300000
        private const val EMPTY_VIEW_TYPE = -0x00400000
        private const val BODY_VIEW_TYPE = -0x00500000

        fun creator(manager: RecyclerView.LayoutManager): SlimAdapter {
            return SlimAdapter(manager)
        }
    }


}
