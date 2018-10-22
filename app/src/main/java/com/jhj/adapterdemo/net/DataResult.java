package com.jhj.adapterdemo.net;

import com.jhj.httplibrary.result.Result;

import org.jetbrains.annotations.NotNull;

/**
 * Created by jhj on 18-10-22.
 */

public class DataResult<T> extends Result<DataResult<T>> {
    @NotNull
    @Override
    public DataResult<T> getClazz() {
        return this;
    }

    private T data;
    private String msg;
    private int result;


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
