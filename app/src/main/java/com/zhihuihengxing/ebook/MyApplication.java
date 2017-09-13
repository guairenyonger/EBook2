package com.zhihuihengxing.ebook;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.zhihuihengxing.ebook.bean.UserInfoBean;

/**
 * Created by Administrator on 2017/3/7 0007.
 */
public class MyApplication extends Application {
    public static UserInfoBean mUserInfoBean;
    public static SharedPreferences mUserInfoDetail;//用户数据
    public static String url="http://192.168.191.1:8080";
    @Override
    public void onCreate() {
        super.onCreate();
        mUserInfoDetail=getSharedPreferences("userinfo", Context.MODE_PRIVATE);//创建本地文件存储用户信息
    }
}
