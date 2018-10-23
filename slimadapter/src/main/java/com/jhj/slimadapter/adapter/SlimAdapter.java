package com.jhj.slimadapter.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jhj.slimadapter.holder.SlimViewHolder;
import com.jhj.slimadapter.holder.ViewInjector;
import com.jhj.slimadapter.listener.OnCustomLayoutListener;
import com.jhj.slimadapter.listener.OnItemClickListener;
import com.jhj.slimadapter.listener.OnItemLongClickListener;
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
 * 简化Adapter
 * <p>
 * 支持普通布局、多样式布局以及添加标题和尾部
 * Created by jhj on 18-10-6.
 */

public class SlimAdapter extends RecyclerView.Adapter<SlimViewHolder> {

    private static final int HEADER_VIEW_TYPE = -0x00100000;
    private static final int FOOTER_VIEW_TYPE = -0x00200000;
    private static final int MORE_VIEW_TYPE = -0x00300000;
    private static final int EMPTY_VIEW_TYPE = -0x00400000;
    private static final int BODY_VIEW_TYPE = -0x00500000;

    private ArrayList<?> dataList;
    private List<Type> dataTypeList = new ArrayList<>();
    private List<Integer> multiTypeList = new ArrayList<>();
    private Map<Type, ItemViewDelegate> itemTypeMap = new HashMap<>();
    private SparseArray<ItemViewDelegate> multiTypeMap = new SparseArray<>();

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private List<View> headerItemViewList = new ArrayList<>();
    private List<View> footerItemViewList = new ArrayList<>();
    private LoadMoreView loadMoreView = new SimpleLoadMoreView();
    private View emptyItemView;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading = false;
    private boolean headerWholeLine = true;
    private boolean footerWholeLine = true;


    private SlimAdapter(RecyclerView.LayoutManager manager) {
        this.layoutManager = manager;
    }


    public static SlimAdapter creator(RecyclerView.LayoutManager manager) {
        return new SlimAdapter(manager);
    }

    @NonNull
    @Override
    public SlimViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (isHeader(viewType)) {//header
            return new SlimViewHolder(headerItemViewList.get(HEADER_VIEW_TYPE - viewType));

        } else if (isFooter(viewType)) {//footer
            return new SlimViewHolder(footerItemViewList.get(FOOTER_VIEW_TYPE - viewType));

        } else if (isLoadMore(viewType)) {//more
            View view = LayoutInflater.from(parent.getContext()).inflate(loadMoreView.getLayoutId(), parent, false);
            return new SlimViewHolder(view);

        } else if (isEmptyView(viewType)) {//empty
            return new SlimViewHolder(emptyItemView);
        } else if (isNormalBody(viewType)) { //normal body
            Type dataType = dataTypeList.get(BODY_VIEW_TYPE - viewType);
            ItemViewDelegate itemView = itemTypeMap.get(dataType);
            if (itemView == null) {
                throw new NullPointerException("missing related layouts corresponding to data types," +
                        "please add related layout:" + dataType);
            }
            int layoutRes = itemView.getItemViewLayoutId();
            return new SlimViewHolder(parent, layoutRes);

        } else if (multiTypeList.contains(viewType)) {//multi body
            ItemViewDelegate itemView = multiTypeMap.get(viewType);
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
        autoLoadMore(position);
        int bodyPosition = position - headerItemViewList.size();

        if (bodyPosition >= 0 && bodyPosition < getDataListCount()) {//body

            Object data = dataList.get(bodyPosition);

            if (data instanceof MultiItemTypeModel) {
                ItemViewDelegate itemView = multiTypeMap.get(((MultiItemTypeModel) data).getItemType());
                itemView.injector(holder.getViewInjector(), data, position);
            } else {
                ItemViewDelegate itemView = itemTypeMap.get(data.getClass());
                itemView.injector(holder.getViewInjector(), data, position);
            }

        } else if (isShowLoadMoreView(position)) {
            loadMoreView.convert(holder);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    int position = recyclerView.getChildAdapterPosition(v);
                    onItemClickListener.onItemClicked(recyclerView, v, position);
                }
            }
        });

         /*
          * 当同时设置了recyclerView 点击和长按事件时，记得要设置长按返回true对事件进行拦截，
          * 否则recyclerView执行完长按事件后会执行点击事件。
          */
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemLongClickListener != null) {
                    int position = recyclerView.getChildAdapterPosition(v);
                    return onItemLongClickListener.onItemLongClicked(recyclerView, v, position);
                }
                return false;
            }
        });
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
                int index = dataTypeList.indexOf(item.getClass());
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
        if (isHeader(type) || isFooter(type) || isLoadMore(type)) {
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
                    if (isLoadMore(viewType)) {
                        return gridLayoutManager.getSpanCount();
                    } else if (isFooter(viewType) && isFooterWholeLine()) {
                        return gridLayoutManager.getSpanCount();
                    } else if (isHeader(viewType) && isHeaderWholeLine()) {
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


    public <D> SlimAdapter register(final int layoutRes, final ItemViewCallback<D> callback) {
        Type type = getDataActualType(callback);
        if (type == null) {
            throw new IllegalArgumentException();
        }
        if (dataTypeList.contains(type)) {
            throw new IllegalArgumentException("The same data type can only use the register() method once.");
        }
        dataTypeList.add(type);
        itemTypeMap.put(type, new ItemViewDelegate<D>() {

            @Override
            public int getItemViewLayoutId() {
                return layoutRes;
            }

            @Override
            public void injector(ViewInjector injector, D data, int position) {
                callback.convert(injector, data, position);
            }
        });

        return this;
    }


    public <D extends MultiItemTypeModel> SlimAdapter register(final int viewType, final int layoutRes, final ItemViewCallback<D> callback) {
        if (multiTypeList.contains(viewType)) {
            throw new IllegalArgumentException("please use different viewType");
        }
        multiTypeList.add(viewType);
        multiTypeMap.put(viewType, new ItemViewDelegate<D>() {
            @Override
            public int getItemViewLayoutId() {
                return layoutRes;
            }

            @Override
            public void injector(ViewInjector injector, D data, int position) {
                callback.convert(injector, data, position);
            }
        });

        return this;
    }


    public SlimAdapter attachTo(final RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(this);
        return this;
    }


    public SlimAdapter addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        if (recyclerView == null) {
            throw new NullPointerException("RecyclerView is null,Please use this method after attachTo(recyclerView)");
        }
        recyclerView.addItemDecoration(itemDecoration);
        return this;
    }


    public SlimAdapter updateData(ArrayList<?> dataList) {
        this.dataList = dataList;
        if (onLoadMoreListener != null) {
            isLoading = false;
            loadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_DEFAULT);
        }
        notifyDataSetChanged();
        return this;
    }

    public SlimAdapter remove(int index) {
        if (dataList == null) {
            throw new NullPointerException("dataList is null,Please use this method after updateData(ArrayList<?>)");
        }
        this.dataList.remove(index);
        notifyItemChanged(index + headerItemViewList.size());
        return this;
    }


    // ====== header ======

    public SlimAdapter addHeader(Context context, int layoutRes, OnCustomLayoutListener listener) {
        View view = LayoutInflater.from(context).inflate(layoutRes, null, false);
        listener.onLayout(this, view);
        return addHeader(view);
    }

    public SlimAdapter addHeader(Context context, int layoutRes) {
        return addHeader(LayoutInflater.from(context).inflate(layoutRes, null, false));
    }

    public SlimAdapter addHeader(View view) {
        ViewGroup.LayoutParams params;
        if (layoutManager instanceof LinearLayoutManager) {
            if (((LinearLayoutManager) layoutManager).getOrientation() == LinearLayout.VERTICAL) {
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

    public SlimAdapter setHeaderWholeLine(boolean headerWholeLine) {
        this.headerWholeLine = headerWholeLine;
        return this;
    }

    public SlimAdapter removeHeader(int index) {
        this.headerItemViewList.remove(index);
        notifyItemChanged(index);
        return this;
    }

    //====== footer ======

    public SlimAdapter addFooter(Context context, int layoutRes, OnCustomLayoutListener listener) {
        View view = LayoutInflater.from(context).inflate(layoutRes, null, false);
        listener.onLayout(this, view);
        return addFooter(view);
    }


    public SlimAdapter addFooter(Context context, int layoutRes) {
        return addFooter(LayoutInflater.from(context).inflate(layoutRes, null, false));
    }

    public SlimAdapter addFooter(View view) {
        ViewGroup.LayoutParams params;
        if (layoutManager instanceof LinearLayoutManager) {
            if (((LinearLayoutManager) layoutManager).getOrientation() == LinearLayout.VERTICAL) {
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

    public SlimAdapter setFooterWholeLine(boolean footerWholeLine) {
        this.footerWholeLine = footerWholeLine;
        return this;
    }

    public SlimAdapter removeFooter(int index) {
        this.headerItemViewList.remove(index);
        notifyItemChanged(index + headerItemViewList.size() + getDataListCount());
        return this;
    }

    //====== load more =========

    public SlimAdapter setLoadMoreView(LoadMoreView loadingView) {
        this.loadMoreView = loadingView;
        return this;
    }


    public void loadMoreEnd() {
        if (onLoadMoreListener == null || getLoadMoreViewPosition() == 0) {
            return;
        }
        isLoading = false;
        loadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_END);
        notifyItemChanged(getLoadMoreViewPosition());

    }


    public void loadMoreFail() {
        if (onLoadMoreListener == null || getLoadMoreViewPosition() == 0) {
            return;
        }
        isLoading = false;
        loadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_FAIL);
        notifyItemChanged(getLoadMoreViewPosition());
    }


    //======empty view =======
    //设置Empty　View 时，该View只在header 、footer、dataList的大小都是０时显示

    public SlimAdapter setEmptyView(View emptyView) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        emptyView.setLayoutParams(params);
        emptyItemView = emptyView;
        notifyDataSetChanged();
        return this;
    }

    public SlimAdapter setEmptyView(Context context, int layoutRes) {
        View view = LayoutInflater.from(context).inflate(layoutRes, null, false);
        setEmptyView(view);
        return this;
    }

    public SlimAdapter setEmptyView(Context context, int layoutRes, OnCustomLayoutListener listener) {
        View view = LayoutInflater.from(context).inflate(layoutRes, null, false);
        listener.onLayout(this, view);
        setEmptyView(view);
        return this;
    }

    // ======= listener =======

    public SlimAdapter setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
        return this;
    }

    public SlimAdapter setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
        return this;
    }


    public SlimAdapter setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        isLoading = false;
        return this;
    }

    //====== 其他 =======


    private void autoLoadMore(int position) {

        if (!isShowLoadMoreView(position)) {
            return;
        }

        if (position < getItemCount() -1){
            return;
        }
        if (loadMoreView.getLoadMoreStatus() != LoadMoreView.STATUS_DEFAULT) {
            return;
        }

        loadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_LOADING);
        if (!isLoading) {
            isLoading = true;
            if (recyclerView != null) {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        onLoadMoreListener.onLoadMore(SlimAdapter.this);
                    }
                });
            } else {
                onLoadMoreListener.onLoadMore(this);
            }
        }

    }

    private boolean isFooterWholeLine() {
        return footerWholeLine;
    }

    private boolean isHeaderWholeLine() {
        return headerWholeLine;
    }


    private boolean isHeader(int viewType) {
        return viewType > FOOTER_VIEW_TYPE && viewType <= HEADER_VIEW_TYPE;
    }

    private boolean isFooter(int viewType) {
        return viewType > MORE_VIEW_TYPE && viewType <= FOOTER_VIEW_TYPE;
    }

    private boolean isLoadMore(int viewType) {
        return viewType == MORE_VIEW_TYPE;
    }

    private boolean isEmptyView(int viewType) {
        return viewType == EMPTY_VIEW_TYPE;
    }

    private boolean isNormalBody(int viewType) {
        return viewType <= BODY_VIEW_TYPE;
    }

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

    private void setFullSpan(RecyclerView.ViewHolder holder) {
        if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder
                    .itemView.getLayoutParams();
            params.setFullSpan(true);
        }
    }


    private <D> Type getDataActualType(ItemViewCallback<D> callback) {
        Type[] interfaces = callback.getClass().getGenericInterfaces();
        for (Type type : interfaces) {
            if (type instanceof ParameterizedType) {
                if (((ParameterizedType) type).getRawType().equals(ItemViewCallback.class)) {
                    Type actualType = ((ParameterizedType) type).getActualTypeArguments()[0];
                    if (actualType instanceof Class) {
                        return actualType;
                    } else {
                        throw new IllegalArgumentException("The generic type argument of Slim is NOT support " +
                                "Generic Parameterized Type now, Please using a WRAPPER class install of it directly.");
                    }
                }
            }
        }
        return null;
    }

}
