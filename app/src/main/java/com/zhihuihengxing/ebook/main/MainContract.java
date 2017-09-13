package com.zhihuihengxing.ebook.main;

import com.zhihuihengxing.ebook.BasePresenter;
import com.zhihuihengxing.ebook.BaseView;
import com.zhihuihengxing.ebook.bean.BookBean;
import com.zhihuihengxing.ebook.bean.RecyclerViewListItemBean;

import java.util.List;

/**
 * Created by Administrator on 2017/3/4 0004.
 */
public interface MainContract {
    /**
     * 与图书收藏有关的父接口
     */
    interface CollectionAction extends BaseView<Presenter>{
        /**
         * 点击收藏
         */
        void addCollectionSucceed(String msg);
        void addCollectionError(String msg);
        /**
         * 取消收藏
         */
        void cancelCollectionSucceed(String msg);
        void cancelCollectionError(String msg);
    }

    /**
     * Libfragment 书城页面
     */
    interface LibView extends CollectionAction{
        /**
         * 获取书城图书信息
         */

        void loadLibDataSucceed(List<RecyclerViewListItemBean> list);
        void loadLibDataError(String msg);
    }

    /**
     * 书详情页面
     */
    interface BookInfoView extends CollectionAction{
        /**
         * 点击下载
         */
        void downloadBookSucceed(String msg);
        void downloadBookSucceedAndOpen(String uri);
        void downloadBookError(String msg);
    }
    /**
     * 按类别显示图书信息
     */
    interface BooksByKindView extends CollectionAction{
        /**
         * 按类别拉取图书数据
         */
        void getDataByKindSucceed(List<BookBean> bookBeans);
        void getDataByKindError(String msg);
    }

    /**
     * 收藏页面
     */
    interface CollectionView extends CollectionAction{
        /**
         * 获取书本信息
         */
        void getDataByCollectionSucceed(List<BookBean> bookBeans);
        void getDataByCollectionError(String mag);
    }

    /**
     * 该Presenter一共服务于
     * 1 Libfragment
     * 2 BookInfoFragment
     * 3 ConllenctionFragment
     * 4 LocalFragment     //这个不用请求网络
     * 这四个页面
     */
    interface Presenter extends BasePresenter{

        /**
         * 用于LibFragment初始化页面数据数据
         */
        void getLibData();

        /**
         *点击收藏按钮
         */
        void addCollection(String bookID);
        /**
         *点击取消收藏
         */
        void cancelCollection(String bookID);

        /**
         * 按类别浏览图书
         */
        void getDataByKind(String bookKind);

        /**
         * 按收藏来获取图书
         */
        void getDataByCollection();
        /**
         * 下载图书到本地
         */
        void downloadBook(String bookUrl,int method);

        void setPresenter(int status);

    }
}
