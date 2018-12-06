package com.jhj.adapterdemo.net;

/**
 * Created by jhj on 18-10-22.
 */

public class DataResult<T> {
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
