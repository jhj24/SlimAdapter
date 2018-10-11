package com.jhj.slimadapter.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jhj on 18-10-6.
 */

public class SlimAdapter extends RecyclerView.Adapter<SlimViewHolder> {

    private List<?> dataList;
    private List<Type> dataTypes = new ArrayList<>();
    private Map<Type, ISlimViewHolder> creators = new HashMap<>();
    private RecyclerView recyclerView;

    private List<SlimViewHolderEx> headerItems = new ArrayList<>();
    private List<SlimViewHolderEx> footerItems = new ArrayList<>();

    private static final int HEADER_VIEW = -0x10000000;
    private static final int FOOTER_VIEW = -0x20000000;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    protected SlimAdapter() {
    }

    public static SlimAdapter creator() {
        return new SlimAdapter();
    }

    @NonNull
    @Override
    public SlimViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType <= HEADER_VIEW && viewType > FOOTER_VIEW) {
            return headerItems.get(HEADER_VIEW - viewType);
        } else if (viewType <= FOOTER_VIEW) {
            return footerItems.get(FOOTER_VIEW - viewType);
        } else {
            //根据item的ViewType获取其实际类型
            Type dataType = dataTypes.get(viewType);
            //根据其实际类型获取 ViewHolder
            ISlimViewHolder viewHolder = creators.get(dataType);
            //缺少该数据类型对应的布局
            if (viewHolder == null) {
                throw new NullPointerException("missing related layouts corresponding to data types,please add related layout:" + dataType);
            }
            return viewHolder.create(parent);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull SlimViewHolder holder, int position) {
        int bodyCount = getItemCount() - (headerItems.size() + footerItems.size());
        int bodyPosition = position - headerItems.size();
        int footerPosition = position - (bodyCount + headerItems.size());

        if (position < headerItems.size()) { //header
            holder.bind(headerItems.get(position), position);
        } else {
            if (bodyPosition < bodyCount) {//body
                holder.bind(dataList.get(bodyPosition), position);
            } else { //footer
                holder.bind(footerItems.get(footerPosition), position);
            }
        }


    }


    @Override
    public int getItemViewType(int position) {
        int bodyCount = getItemCount() - (headerItems.size() + footerItems.size());
        int bodyPosition = position - headerItems.size();
        int footerPosition = position - (bodyCount + headerItems.size());

        if (position < headerItems.size()) { //header
            return HEADER_VIEW - position;
        } else {
            if (bodyPosition < bodyCount) {//body
                Object item = dataList.get(bodyPosition);
                int index = dataTypes.indexOf(item.getClass());
                if (index == -1) {
                    dataTypes.add(item.getClass());
                }
                return dataTypes.indexOf(item.getClass());
            } else { //footer
                return FOOTER_VIEW - footerPosition;
            }
        }
    }


    @Override
    public int getItemCount() {
        if (dataList == null) {
            return headerItems.size() + footerItems.size();
        } else {
            return dataList.size() + headerItems.size() + footerItems.size();
        }
    }

    public <D> SlimAdapter register(final int layoutRes, final SlimInjector<D> slimInjector) {
        Type type = getSlimInjectorActualTypeArguments(slimInjector);
        if (type == null) {
            throw new IllegalArgumentException();
        }


        creators.put(type, new ISlimViewHolder() {
            @Override
            public SlimViewHolder<D> create(ViewGroup parent) {

                return new SlimViewHolder<D>(parent, layoutRes) {


                    @Override
                    void onBind(D data, IViewInjector injector, int pos) {
                        slimInjector.onInject(data, injector, pos);
                    }
                };

            }
        });
        return this;
    }


    public SlimAdapter attachTo(final RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        setItemListener();
        recyclerView.setAdapter(this);
        return this;
    }

    public SlimAdapter layoutManager(RecyclerView.LayoutManager manager) {
        if (recyclerView == null) {
            throw new NullPointerException("RecyclerView is null,Please use this method after attachTo(recyclerView)");
        }
        recyclerView.setLayoutManager(manager);
        return this;
    }

    public SlimAdapter addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        if (recyclerView == null) {
            throw new NullPointerException("RecyclerView is null,Please use this method after attachTo(recyclerView)");
        }
        recyclerView.addItemDecoration(itemDecoration);
        return this;
    }

    public SlimAdapter setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
        return this;
    }

    public SlimAdapter setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
        return this;
    }


    public SlimAdapter updateData(List<?> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
        return this;
    }


    public SlimAdapter addHeader(Context context, int layoutRes, OnCustomLayoutListener listener) {
        View view = LayoutInflater.from(context).inflate(layoutRes, null, false);
        listener.onLayout(view);
        return addHeader(view);
    }

    public SlimAdapter addHeader(Context context, int layoutRes) {
        return addHeader(LayoutInflater.from(context).inflate(layoutRes, null, false));
    }

    public SlimAdapter addHeader(View view) {
        headerItems.add(new SlimViewHolderEx(view));
        notifyDataSetChanged();
        return this;
    }


    public SlimAdapter addFooter(Context context, int layoutRes, OnCustomLayoutListener listener) {
        View view = LayoutInflater.from(context).inflate(layoutRes, null, false);
        listener.onLayout(view);
        return addFooter(view);
    }


    public SlimAdapter addFooter(Context context, int layoutRes) {
        return addFooter(LayoutInflater.from(context).inflate(layoutRes, null, false));
    }

    public SlimAdapter addFooter(View view) {
        footerItems.add(new SlimViewHolderEx(view));
        notifyDataSetChanged();
        return this;
    }

    private void setItemListener() {
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                view.setOnClickListener(new View.OnClickListener() {
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
                view.setOnLongClickListener(new View.OnLongClickListener() {
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
            public void onChildViewDetachedFromWindow(View view) {

            }
        });
    }


    <D> Type getSlimInjectorActualTypeArguments(SlimInjector<D> slimInjector) {
        Type[] interfaces = slimInjector.getClass().getGenericInterfaces();
        for (Type type : interfaces) {
            if (type instanceof ParameterizedType) {
                if (((ParameterizedType) type).getRawType().equals(SlimInjector.class)) {
                    Type actualType = ((ParameterizedType) type).getActualTypeArguments()[0];
                    if (actualType instanceof Class) {
                        return actualType;
                    } else {
                        throw new IllegalArgumentException("The generic type argument of SlimInjector is NOT support Generic Parameterized Type now, Please using a WRAPPER class install of it directly.");
                    }
                }
            }
        }
        return null;
    }


    interface ISlimViewHolder<D> {
        SlimViewHolder<D> create(ViewGroup parent);
    }

    class SlimViewHolderEx extends SlimViewHolder {

        SlimViewHolderEx(View view) {
            super(view);
        }

        @Override
        void onBind(Object data, IViewInjector injector, int pos) {

        }
    }


}
