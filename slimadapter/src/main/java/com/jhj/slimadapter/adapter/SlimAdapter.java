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

    private ArrayList<?> dataList;
    private List<Type> dataTypes = new ArrayList<>();
    private Map<Type, SlimInjector> itemTypeMap = new HashMap<>();
    private RecyclerView recyclerView;

    private List<View> headerItems = new ArrayList<>();
    private List<View> footerItems = new ArrayList<>();

    private static final int HEADER_VIEW_TYPE = -0x10000000;
    private static final int FOOTER_VIEW_TYPE = -0x20000000;
    private static final int BODY_VIEW_TYPE = -0x30000000;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;


    public static SlimAdapter creator() {

        return new SlimAdapter();
    }

    @NonNull
    @Override
    public SlimViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        if (viewType > FOOTER_VIEW_TYPE && viewType <= HEADER_VIEW_TYPE) {
            return new SlimViewHolder(headerItems.get(HEADER_VIEW_TYPE - viewType));
        } else if (viewType > BODY_VIEW_TYPE && viewType <= FOOTER_VIEW_TYPE) {
            return new SlimViewHolder(footerItems.get(FOOTER_VIEW_TYPE - viewType));
        } else {
            Type dataType = dataTypes.get(BODY_VIEW_TYPE - viewType);
            SlimInjector injector = itemTypeMap.get(dataType);
            if (injector == null) {
                throw new NullPointerException("missing related layouts corresponding to data types,please add related layout:" + dataType);
            }
            int layoutRes = injector.getItemViewLayoutId();
            //根据其实际类型获取 ViewHolder
            return new SlimViewHolder(parent, layoutRes);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull SlimViewHolder holder, int position) {
        int bodyCount = getItemCount() - (headerItems.size() + footerItems.size());
        int bodyPosition = position - headerItems.size();
        int footerPosition = position - (bodyCount + headerItems.size());

        if (position < headerItems.size()) { //header
            // convert(holder, headerItems.get(position), position);
        } else {
            if (bodyPosition < bodyCount) {//body
                convert(holder, dataList.get(bodyPosition), position);
            } else { //footer
                //  convert(holder, footerItems.get(footerPosition), position);
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        int bodyCount = getItemCount() - (headerItems.size() + footerItems.size());
        int bodyPosition = position - headerItems.size();
        int footerPosition = position - (bodyCount + headerItems.size());

        if (position < headerItems.size()) { //header
            return HEADER_VIEW_TYPE - position;
        } else {
            if (bodyPosition < bodyCount) {//body
                Object item = dataList.get(bodyPosition);
                int index = dataTypes.indexOf(item.getClass());
                if (index == -1) {
                    dataTypes.add(item.getClass());
                }
                index = dataTypes.indexOf(item.getClass());
                return BODY_VIEW_TYPE - index;
            } else { //footer
                return FOOTER_VIEW_TYPE - footerPosition;
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


    public <D> void convert(SlimViewHolder holder, D item, int position) {
        SlimInjector itemView = itemTypeMap.get(item.getClass());
        itemView.convert(holder, item, position);
    }

    public interface Slim<D> {
        void a(SlimViewHolder holder, D t, int position);
    }


    public <D> SlimAdapter register(final int layoutRes, final Slim<D> slim) {
        Type type = getSlimInjectorActualTypeArguments(slim);
        if (type == null) {
            throw new IllegalArgumentException();
        }
        itemTypeMap.put(type, new SlimInjector<D>() {
            @Override
            public int getItemViewLayoutId() {
                return layoutRes;
            }

            @Override
            public void convert(SlimViewHolder holder, D data, int position) {
                slim.a(holder, data, position);
            }
        });

        return this;
    }

   /* public <D extends MultiItemTypeModel> SlimAdapter register(final Map<Integer, Integer> layoutRes, final Slim<D> slim) {
        final Type type = getSlimInjectorActualTypeArguments(slim);
        if (type == null) {
            throw new IllegalArgumentException();
        }
        itemTypeMap.put(type, new SlimInjector<D>() {
            @Override
            public int getItemViewLayoutId(D d) {
                for (Map.Entry<Integer, Integer> entry : layoutRes.entrySet()) {
                    if (entry.getValue() == getItemViewType(d.getItemType())) {
                        return entry.getValue();
                    }
                }
                return 0;
            }


            @Override
            public void convert(SlimViewHolder holder, D data, int position) {
                slim.a(holder, data, position);
            }

        });

        return this;
    }*/


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


    public SlimAdapter updateData(ArrayList<?> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
        return this;
    }

    public SlimAdapter remove(int index) {
        if (dataList == null) {
            throw new NullPointerException("dataList is null,Please use this method after updateData(ArrayList<?>)");
        }
        this.dataList.remove(index);
        notifyItemChanged(index + headerItems.size());
        return this;
    }

    public SlimAdapter removeHeader(int index) {
        if (dataList == null) {
            throw new NullPointerException("dataList is null,Please use this method after updateData(ArrayList<?>)");
        }
        this.headerItems.remove(index);
        notifyItemChanged(index);
        return this;
    }

    public SlimAdapter removeFooter(int index) {
        if (dataList == null) {
            throw new NullPointerException("dataList is null,Please use this method after updateData(ArrayList<?>)");
        }
        this.headerItems.remove(index);
        notifyItemChanged(index + headerItems.size() + dataList.size());
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
        headerItems.add(view);
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
        footerItems.add(view);
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


    private <D> Type getSlimInjectorActualTypeArguments(Slim<D> slimInjector) {
        Type[] interfaces = slimInjector.getClass().getGenericInterfaces();
        for (Type type : interfaces) {
            if (type instanceof ParameterizedType) {
                if (((ParameterizedType) type).getRawType().equals(Slim.class)) {
                    Type actualType = ((ParameterizedType) type).getActualTypeArguments()[0];
                    if (actualType instanceof Class) {
                        return actualType;
                    } else {
                        throw new IllegalArgumentException("The generic type argument of Slim is NOT support Generic Parameterized Type now, Please using a WRAPPER class install of it directly.");
                    }
                }
            }
        }
        return null;
    }


}
