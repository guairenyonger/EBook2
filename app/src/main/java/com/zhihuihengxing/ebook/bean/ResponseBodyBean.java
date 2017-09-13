package com.zhihuihengxing.ebook.bean;

import retrofit2.Response;

/**
 * Created by Administrator on 2017/4/4 0004.
 */
public class ResponseBodyBean {
    private Response<okhttp3.ResponseBody> response;
    private String bookName;

    public Response<okhttp3.ResponseBody> getResponse() {
        return response;
    }

    public void setResponse(Response<okhttp3.ResponseBody> response) {
        this.response = response;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
}
