package com.zhihuihengxing.ebook.data.entity;

/**
 * Created by Administrator on 2017/3/7 0007.
 */
public class CancelCollectionBody {
    public String bookID;
    public String userID;
    public CancelCollectionBody(String bookID, String userID){
        this.bookID=bookID;
        this.userID=userID;
    }
}
