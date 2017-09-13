package com.zhihuihengxing.ebook.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/2/26 0026.
 */
public class RecyclerViewListItemBean implements Serializable{

    /*
    * 主页面RV控件的item
    * */
    private String kindID;
    private String kindName;
    private String describe;
    private List<BookBean> listItemChildren;

    public String getKindID() {
        return kindID;
    }

    public void setKindID(String kindId) {
        this.kindID = kindId;
    }

    public String getKindName() {
        return kindName;
    }

    public void setKindName(String kindName) {
        this.kindName = kindName;
    }


    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public List<BookBean> getListItemChildren() {
        return listItemChildren;
    }

    public void setListItemChildren(List<BookBean> listItemChildren) {
        this.listItemChildren = listItemChildren;
    }
}
