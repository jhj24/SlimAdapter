package com.jhj.slimadapter.adapter;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
    private Handler handler = new Handler();
    private RecyclerView recyclerView;

    private SlimAdapter() {
    }

    public static SlimAdapter creator() {
        return new SlimAdapter();
    }

    @NonNull
    @Override
    public SlimViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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


    @Override
    public void onBindViewHolder(@NonNull SlimViewHolder holder, int position) {
        holder.bind(dataList.get(position), position);
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

    private <D> Type getSlimInjectorActualTypeArguments(SlimInjector<D> slimInjector) {
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

    public SlimAdapter attachTo(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
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


    public SlimAdapter updateData(List<?> dataList) {
        this.dataList = dataList;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            notifyDataSetChanged();
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
        return this;
    }

    /**
     * 返回position位置的ViewType，
     * <p>
     * dataList中可能有多种数据类型，根据其数据类型返回其不同的ViewType，ViewType从0开始一直向后加
     *
     * @param position item位置
     * @return type
     */
    @Override
    public int getItemViewType(int position) {
        Object item = dataList.get(position);
        int index = dataTypes.indexOf(item.getClass());
        if (index == -1) {
            dataTypes.add(item.getClass());
        }
        return dataTypes.indexOf(item.getClass());
    }


    @Override
    public int getItemCount() {
        if (dataList == null) {
            return 0;
        } else {
            return dataList.size();
        }
    }


    interface ISlimViewHolder<D> {
        SlimViewHolder<D> create(ViewGroup parent);
    }


}
