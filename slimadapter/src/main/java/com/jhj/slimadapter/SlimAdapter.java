package com.jhj.slimadapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jhj.slimadapter.callback.ItemViewBind;
import com.jhj.slimadapter.callback.ItemViewDelegate;
import com.jhj.slimadapter.holder.SlimViewHolder;
import com.jhj.slimadapter.holder.ViewInjector;
import com.jhj.slimadapter.itemtouch.ItemTouchHelperCallback;
import com.jhj.slimadapter.listener.OnCustomLayoutListener;
import com.jhj.slimadapter.listener.OnItemDragListener;
import com.jhj.slimadapter.listener.OnItemSwipeListener;
import com.jhj.slimadapter.listener.OnLoadMoreListener;
import com.jhj.slimadapter.model.MultiItemTypeModel;
import com.jhj.slimadapter.more.LoadMoreView;
import com.jhj.slimadapter.more.SimpleLoadMoreView;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jhj on 18-10-25.
 */

public class SlimAdapter extends RecyclerView.Adapter<SlimViewHolder> {


    private static final int HEADER_VIEW_TYPE = -0x00100000;
    private static final int FOOTER_VIEW_TYPE = -0x00200000;
    private static final int MORE_VIEW_TYPE = -0x00300000;
    private static final int EMPTY_VIEW_TYPE = -0x00400000;
    private static final int BODY_VIEW_TYPE = -0x00500000;

    private Type genericActualType;
    private List<Type> dataViewTypeList = new ArrayList<>();
    private List<Integer> multiViewTypeList = new ArrayList<>();
    private Map<Type, ItemViewDelegate> itemViewMap = new HashMap<>();
    private SparseArray<ItemViewDelegate> multiViewMap = new SparseArray<>();

    private List dataList = new ArrayList();
    private RecyclerView recyclerView;


    private List<View> headerItemViewList = new ArrayList<>();
    private List<View> footerItemViewList = new ArrayList<>();
    private LoadMoreView loadMoreView = new SimpleLoadMoreView();
    private View emptyItemView;


    private OnLoadMoreListener onLoadMoreListener;
    private boolean headerWholeLine = true;
    private boolean footerWholeLine = true;


    private boolean itemDragEnabled = false;
    private boolean itemSwipeEnabled = false;
    private ItemTouchHelperCallback itemTouchHelperCallback;

    public RecyclerView getRecyclerView() {
        if (recyclerView == null) {
            throw new NullPointerException("RecyclerView is null,Please first use attachTo(recyclerView)");
        }
        return recyclerView;
    }

    public List getDataList() {
        return dataList;
    }

    private SlimAdapter() {

    }

    public static SlimAdapter creator() {
        return new SlimAdapter();
    }

    @SuppressWarnings("unchecked")
    public <D> SlimAdapter register(@LayoutRes final int layoutRes, final ItemViewBind<D> bind) {
        Type type;
        if (getGenericActualType() == null) {
            type = getDataActualType(bind);
        } else {
            type = getGenericActualType();
        }
        if (type == null) {
            throw new IllegalArgumentException();
        }
        if (dataViewTypeList.contains(type)) {
            throw new IllegalArgumentException("The same data type can only use the register() method once.");
        }
        dataViewTypeList.add(type);
        itemViewMap.put(type, new ItemViewDelegate<D>() {

            @Override
            public int getItemViewLayoutId() {
                return layoutRes;
            }

            @Override
            public void injector(@NonNull ViewInjector injector, D data, int position) {
                bind.convert(SlimAdapter.this, injector, data, position);
            }
        });

        return this;
    }

    @SuppressWarnings("unchecked")
    public <D extends MultiItemTypeModel> SlimAdapter register(final int viewType, @LayoutRes final int layoutRes, final ItemViewBind<D> bind) {
        if (multiViewTypeList.contains(viewType)) {
            throw new IllegalArgumentException("please use different viewType");
        }
        multiViewTypeList.add(viewType);
        multiViewMap.put(viewType, new ItemViewDelegate<D>() {
            @Override
            public int getItemViewLayoutId() {
                return layoutRes;
            }

            @Override
            public void injector(@NonNull ViewInjector injector, D data, int position) {
                bind.convert(SlimAdapter.this, injector, data, position);
            }
        });

        return this;
    }

    @SuppressWarnings("unchecked")
    public SlimAdapter attachTo(final RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        recyclerView.setAdapter(this);
        return this;
    }

    @SuppressWarnings("unchecked")
    public SlimAdapter addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        getRecyclerView().addItemDecoration(itemDecoration);
        return this;
    }

    @SuppressWarnings("unchecked")
    public SlimAdapter setDataList(@NonNull List<?> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
        resetLoadMoreStates();
        return this;
    }

    @SuppressWarnings("unchecked")
    public SlimAdapter addDataList(@NonNull List<?> dataList) {
        int startIndex = this.dataList.size() + getHeaderViewCount();
        this.dataList.addAll(dataList);
        notifyItemRangeInserted(startIndex, dataList.size());
        resetLoadMoreStates();
        return this;
    }

    @SuppressWarnings("unchecked")
    public SlimAdapter addDataList(int index, @NonNull List<?> dataList) {
        this.dataList.addAll(index, dataList);
        notifyItemRangeInserted(index + getHeaderViewCount(), dataList.size());
        resetLoadMoreStates();
        return this;
    }

    @SuppressWarnings("unchecked")
    public SlimAdapter addData(@NonNull Object data) {
        int startIndex = this.dataList.size() + getHeaderViewCount();
        this.dataList.add(data);
        notifyItemInserted(startIndex);
        resetLoadMoreStates();
        return this;
    }

    @SuppressWarnings("unchecked")
    public SlimAdapter addData(int index, @NonNull Object data) {
        this.dataList.add(index, data);
        notifyItemInserted(index + getHeaderViewCount());
        resetLoadMoreStates();
        return this;
    }


    @SuppressWarnings("unchecked")
    public SlimAdapter remove(int index) {
        this.dataList.remove(index);
        notifyItemChanged(index + headerItemViewList.size());
        return this;
    }


    // ====== header ======

    @SuppressWarnings("unchecked")
    public SlimAdapter addHeader(Context context, int layoutRes, OnCustomLayoutListener listener) {
        View view = LayoutInflater.from(context).inflate(layoutRes, null, false);
        listener.onLayout(this, view);
        return addHeader(view);
    }

    public SlimAdapter addHeader(Context context, int layoutRes) {
        return addHeader(LayoutInflater.from(context).inflate(layoutRes, null, false));
    }

    @SuppressWarnings("unchecked")
    public SlimAdapter addHeader(View view) {
        ViewGroup.LayoutParams params;
        if (getRecyclerView().getLayoutManager() instanceof LinearLayoutManager) {
            if (((LinearLayoutManager) getRecyclerView().getLayoutManager()).getOrientation() == LinearLayout.VERTICAL) {
                params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
            view.setLayoutParams(params);
        }
        headerItemViewList.add(view);
        notifyDataSetChanged();
        return this;
    }

    @SuppressWarnings("unchecked")
    public SlimAdapter removeHeader(int index) {
        this.headerItemViewList.remove(index);
        notifyItemChanged(index);
        return this;
    }

    @SuppressWarnings("unchecked")
    public SlimAdapter setHeaderWholeLine(boolean headerWholeLine) {
        this.headerWholeLine = headerWholeLine;
        return this;
    }

    //====== footer ======
    @SuppressWarnings("unchecked")
    public SlimAdapter addFooter(Context context, int layoutRes, OnCustomLayoutListener listener) {
        View view = LayoutInflater.from(context).inflate(layoutRes, null, false);
        listener.onLayout(this, view);
        return addFooter(view);
    }


    public SlimAdapter addFooter(Context context, int layoutRes) {
        return addFooter(LayoutInflater.from(context).inflate(layoutRes, null, false));
    }

    @SuppressWarnings("unchecked")
    public SlimAdapter addFooter(View view) {
        ViewGroup.LayoutParams params;
        if (getRecyclerView().getLayoutManager() instanceof LinearLayoutManager) {
            if (((LinearLayoutManager) getRecyclerView().getLayoutManager()).getOrientation() == LinearLayout.VERTICAL) {
                params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
            view.setLayoutParams(params);
        }
        footerItemViewList.add(view);
        notifyDataSetChanged();
        return this;
    }

    @SuppressWarnings("unchecked")
    public SlimAdapter removeFooter(int index) {
        this.headerItemViewList.remove(index);
        notifyItemChanged(index + headerItemViewList.size() + getDataListCount());
        return this;
    }

    @SuppressWarnings("unchecked")
    public SlimAdapter setFooterWholeLine(boolean footerWholeLine) {
        this.footerWholeLine = footerWholeLine;
        return this;
    }

    //====== load more =========

    @SuppressWarnings("unchecked")
    public SlimAdapter setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        return this;
    }

    @SuppressWarnings("unchecked")
    public SlimAdapter setLoadMoreView(LoadMoreView loadingView) {
        this.loadMoreView = loadingView;
        return this;
    }

    public void loadMoreEnd() {
        if (onLoadMoreListener == null || getLoadMoreViewPosition() == 0) {
            return;
        }
        loadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_END);
        notifyItemChanged(getLoadMoreViewPosition());
    }


    public void loadMoreFail() {
        if (onLoadMoreListener == null || getLoadMoreViewPosition() == 0) {
            return;
        }
        loadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_FAIL);
        notifyItemChanged(getLoadMoreViewPosition());
    }

    public void loadMoreFail(LoadMoreView.OnLoadMoreFailClickClListener listener) {
        loadMoreFail();
        loadMoreView.setLoadFailOnClickListener(listener);
    }

    private void resetLoadMoreStates() {
        if (onLoadMoreListener != null) {
            loadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_LOADING);
            notifyItemChanged(getLoadMoreViewPosition());
        }
    }


    //======empty view =======
    //设置Empty　View 时，该View只在header 、footer、dataList的大小都是０时显示

    @SuppressWarnings("unchecked")
    public SlimAdapter setEmptyView(Context context, int layoutRes, OnCustomLayoutListener listener) {
        View view = LayoutInflater.from(context).inflate(layoutRes, null, false);
        listener.onLayout(this, view);
        setEmptyView(view);
        return this;
    }

    @SuppressWarnings("unchecked")
    public SlimAdapter setEmptyView(Context context, int layoutRes) {
        View view = LayoutInflater.from(context).inflate(layoutRes, null, false);
        setEmptyView(view);
        return this;
    }

    @SuppressWarnings("unchecked")
    public SlimAdapter setEmptyView(View emptyView) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        emptyView.setLayoutParams(params);
        emptyItemView = emptyView;
        notifyDataSetChanged();
        return this;
    }

    private Type getGenericActualType() {
        return genericActualType;
    }
    //


    public SlimAdapter setItemTouchHelper() {
        itemTouchHelperCallback = new ItemTouchHelperCallback(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(getRecyclerView());
        return this;
    }

    //======　drag　======


    public SlimAdapter setDragItem(boolean isDrag) {
        this.itemDragEnabled = isDrag;
        return this;
    }

    public SlimAdapter setDragFlags(int dragFlags) {
        itemTouchHelperCallback.setDragFlags(dragFlags);
        return this;
    }

    public SlimAdapter setOnItemDragListener(OnItemDragListener onItemDragListener) {
        itemTouchHelperCallback.setOnItemDragListener(onItemDragListener);
        return this;
    }

    public SlimAdapter setMoveThreshold(float moveThreshold) {
        itemTouchHelperCallback.setMoveThreshold(moveThreshold);
        return this;
    }

    public boolean isLongPressDragEnable() {
        return itemDragEnabled;
    }

    //======= swipe =======


    public SlimAdapter setSwipeItem(boolean isSwipe) {
        itemSwipeEnabled = isSwipe;
        return this;
    }


    public SlimAdapter setSwipeFlags(int swipeFlags) {
        itemTouchHelperCallback.setSwipeFlags(swipeFlags);
        return this;
    }

    public SlimAdapter setOnItemSwipeListener(OnItemSwipeListener listener) {
        itemTouchHelperCallback.setOnItemSwipeListener(listener);
        return this;
    }

    public SlimAdapter setSwipeThreshold(float swipeThreshold) {
        itemTouchHelperCallback.setSwipeThreshold(swipeThreshold);
        return this;
    }

    public SlimAdapter setSwipeFadeOutAnim(boolean swipeFadeOutAnim) {
        itemTouchHelperCallback.setSwipeFadeOutAnim(swipeFadeOutAnim);
        return this;
    }

    public boolean isItemSwipeEnable() {
        return itemSwipeEnabled;
    }


    /**
     * 二次泛型封装会出现错误，可以通过该方法获取实际类型
     *
     * @return 泛型的实际类型
     */
    @SuppressWarnings("unchecked")
    public SlimAdapter setGenericActualType(Type genericActualType) {
        this.genericActualType = genericActualType;
        return this;
    }


    @NonNull
    @Override
    public SlimViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (isHeaderView(viewType)) {//header
            return new SlimViewHolder(headerItemViewList.get(HEADER_VIEW_TYPE - viewType));

        } else if (isFooterView(viewType)) {//footer
            return new SlimViewHolder(footerItemViewList.get(FOOTER_VIEW_TYPE - viewType));

        } else if (isLoadMoreView(viewType)) {//more
            View view = LayoutInflater.from(parent.getContext()).inflate(loadMoreView.getLayoutId(), parent, false);
            return new SlimViewHolder(view);

        } else if (isEmptyView(viewType)) {//empty
            return new SlimViewHolder(emptyItemView);
        } else if (isNormalBodyView(viewType)) { //normal body
            Type dataType = dataViewTypeList.get(BODY_VIEW_TYPE - viewType);
            ItemViewDelegate itemView = itemViewMap.get(dataType);
            if (itemView == null) {
                throw new NullPointerException("missing related layouts corresponding to data types," +
                        "please add related layout:" + dataType);
            }
            int layoutRes = itemView.getItemViewLayoutId();
            return new SlimViewHolder(parent, layoutRes);

        } else if (multiViewTypeList.contains(viewType)) {//multi body
            ItemViewDelegate itemView = multiViewMap.get(viewType);
            if (itemView == null) {
                throw new NullPointerException("Because you used a multi-style layout to inherit the com.jhj.slimadapter.model.MutilItemTypeModel" +
                        ", But did not find the layout corresponding to the return value of the getItemType() method.");
            }
            int layoutRes = itemView.getItemViewLayoutId();
            return new SlimViewHolder(parent, layoutRes);

        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SlimViewHolder holder, int position) {

        int bodyPosition = position - headerItemViewList.size();

        if (bodyPosition >= 0 && bodyPosition < getDataListCount()) {//body

            Object data = dataList.get(bodyPosition);

            if (data instanceof MultiItemTypeModel) {
                ItemViewDelegate itemView = multiViewMap.get(((MultiItemTypeModel) data).getItemType());
                if (itemView != null) {
                    itemView.injector(holder.getViewInjector(), data, position);
                }
            } else {
                ItemViewDelegate itemView = itemViewMap.get(data.getClass());
                if (itemView != null) {
                    itemView.injector(holder.getViewInjector(), data, position);
                }
            }

        } else if (isShowLoadMoreView(position)) {

            autoLoadMore();
            loadMoreView.convert(holder);
        }
    }

    @Override
    public int getItemViewType(int position) {

        int bodyPosition = position - headerItemViewList.size();
        int footerPosition = position - (getDataListCount() + headerItemViewList.size());

        if (position < headerItemViewList.size()) { //header
            return HEADER_VIEW_TYPE - position;

        } else if (bodyPosition >= 0 && bodyPosition < getDataListCount()) { //body
            Object item = dataList.get(bodyPosition);
            if (item instanceof MultiItemTypeModel) { //多样式布局
                return ((MultiItemTypeModel) item).getItemType();

            } else {//普通布局
                int index = dataViewTypeList.indexOf(item.getClass());
                return BODY_VIEW_TYPE - index;
            }

        } else if (position >= headerItemViewList.size() + getDataListCount() &&
                position < getDataListCount() + headerItemViewList.size() + footerItemViewList.size()) { //footer
            return FOOTER_VIEW_TYPE - footerPosition;

        } else if (isShowLoadMoreView(position)) { //more
            return MORE_VIEW_TYPE;

        } else if (emptyItemView != null && (getDataListCount() + headerItemViewList.size() + footerItemViewList.size()) == 0) {  //empty
            return EMPTY_VIEW_TYPE;

        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public int getItemCount() {
        if (emptyItemView != null && getLoadMoreViewPosition() == 0) {
            return 1;
        }
        if (onLoadMoreListener != null) {
            if (getLoadMoreViewPosition() == 0) {
                return 0;
            } else {
                return getDataListCount() + headerItemViewList.size() + footerItemViewList.size() + 1;
            }
        } else {
            return getDataListCount() + headerItemViewList.size() + footerItemViewList.size();
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull SlimViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int type = holder.getItemViewType();
        if (isHeaderView(type) || isFooterView(type) || isLoadMoreView(type) || isEmptyView(type)) {
            setFullSpan(holder);
        } else {
            //addAnimation(holder);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    if (isLoadMoreView(viewType)) {
                        return gridLayoutManager.getSpanCount();
                    } else if (isFooterView(viewType) && isFooterWholeLine()) {
                        return gridLayoutManager.getSpanCount();
                    } else if (isHeaderView(viewType) && isHeaderWholeLine()) {
                        return gridLayoutManager.getSpanCount();
                    } else if (isEmptyView(viewType)) {
                        return gridLayoutManager.getSpanCount();
                    }

                    if (spanSizeLookup != null)
                        return spanSizeLookup.getSpanSize(position);
                    return 1;
                }
            });
            gridLayoutManager.setSpanCount(gridLayoutManager.getSpanCount());
        }

    }

    //========= ViewType判断 ========
    public boolean isHeaderView(int viewType) {
        return viewType > FOOTER_VIEW_TYPE && viewType <= HEADER_VIEW_TYPE;
    }

    public boolean isFooterView(int viewType) {
        return viewType > MORE_VIEW_TYPE && viewType <= FOOTER_VIEW_TYPE;
    }

    public boolean isLoadMoreView(int viewType) {
        return viewType == MORE_VIEW_TYPE;
    }

    public boolean isEmptyView(int viewType) {
        return viewType == EMPTY_VIEW_TYPE;
    }

    public boolean isNormalBodyView(int viewType) {
        return viewType <= BODY_VIEW_TYPE;
    }

    public int getHeaderViewCount() {
        return headerItemViewList.size();
    }

    //====== 其他 =======


    @SuppressWarnings("unchecked")
    private void autoLoadMore() {

        if (loadMoreView.getLoadMoreStatus() != LoadMoreView.STATUS_LOADING) {
            return;
        }

        loadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_LOADING);

        if (recyclerView != null) {
            getRecyclerView().post(new Runnable() {
                @Override
                public void run() {
                    onLoadMoreListener.onLoadMore(SlimAdapter.this);
                }
            });
        } else {
            onLoadMoreListener.onLoadMore(this);
        }

    }


    /**
     * 是否加载更多，当位于０位置的时候，不显示
     *
     * @param position 　position
     * @return boolean
     */
    private boolean isShowLoadMoreView(int position) {
        return onLoadMoreListener != null && position != 0 && position == getLoadMoreViewPosition();
    }

    private int getLoadMoreViewPosition() {
        return headerItemViewList.size() + getDataListCount() + footerItemViewList.size();
    }

    private int getDataListCount() {
        if (dataList == null) {
            return 0;
        } else {
            return dataList.size();
        }
    }

    private boolean isHeaderWholeLine() {
        return headerWholeLine;
    }

    private boolean isFooterWholeLine() {
        return footerWholeLine;
    }


    private void setFullSpan(RecyclerView.ViewHolder holder) {
        if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder
                    .itemView.getLayoutParams();
            params.setFullSpan(true);
        }
    }


    private <D> Type getDataActualType(ItemViewBind<D> bind) {
        Type[] interfaces = bind.getClass().getGenericInterfaces();
        for (Type type : interfaces) {
            if (type instanceof ParameterizedType) {
                if (((ParameterizedType) type).getRawType().equals(ItemViewBind.class)) {
                    Type actualType = ((ParameterizedType) type).getActualTypeArguments()[0];
                    if (actualType instanceof Class) {
                        return actualType;
                    } else {
                        throw new IllegalArgumentException("The generic type argument of Slim is NOT support " +
                                "Generic Parameterized Type now, Please getGenericActualType() method return Actual type.");
                    }
                }
            }
        }
        return null;
    }

}
