package com.zhihuihengxing.ebook.data.entity;

/**
 * Created by Administrator on 2017/3/6 0006.
 */
public class AddCollectionBody {
    public String bookID;
    public String userID;
    public AddCollectionBody(String bookID,String userID){
        this.bookID=bookID;
        this.userID=userID;
    }
}
