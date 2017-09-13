package com.zhihuihengxing.ebook.data.entity;

/**
 * Created by Administrator on 2017/3/6 0006.
 */
public class HttpResult<T> {
    private int state;
    private String message;
    //Data
    private T date;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getDate() {
        return date;
    }

    public void setDate(T date) {
        this.date = date;
    }
}
