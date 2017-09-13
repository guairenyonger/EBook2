package com.zhihuihengxing.ebook.utils;

import android.content.SharedPreferences;
import android.util.Log;

import com.zhihuihengxing.ebook.MyApplication;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/27 0027.
 */
public class UserInfoSPUtil {
    //存储用户信息
    public static void saveUserInfo(String userID,String userName,String password){
        SharedPreferences.Editor editor= MyApplication.mUserInfoDetail.edit();
        editor.putString("userID",userID);
        editor.putString("userName",userName);
        editor.putString("password", password);
        editor.commit();
    }
    //读取用户信息
    public static Map<String,String> loadUserInfo(){
        Map<String,String> userInfoMap=new HashMap<>();//存储用户 账号 和 密码
        if(MyApplication.mUserInfoDetail!=null){
            userInfoMap.put("userID", MyApplication.mUserInfoDetail.getString("userID", null));
            userInfoMap.put("userName", MyApplication.mUserInfoDetail.getString("userName", null));
            userInfoMap.put("password", MyApplication.mUserInfoDetail.getString("password", null));
        }
        return userInfoMap;

    }
    //清除用户数据
    public static void clearUserInfo(){
        SharedPreferences.Editor editor= MyApplication.mUserInfoDetail.edit();
        editor.remove("userID");
        editor.remove("password");
        editor.remove("userName");
        editor.commit();
    }


    //应用配置 夜间模式
    public static void setNight(boolean b){
        SharedPreferences.Editor editor= MyApplication.mUserInfoDetail.edit();
        editor.putBoolean("night", b);
        editor.commit();
    }
    public static boolean getNight(){
        boolean b=MyApplication.mUserInfoDetail.getBoolean("night",false);
        return b;
    }
    /*
    *应用配置 设置背景
    */
    public static void setBg(int i){
        SharedPreferences.Editor editor= MyApplication.mUserInfoDetail.edit();
        editor.putInt("bg", i);
        editor.commit();
    }
    public static int getBg(){
        int i=MyApplication.mUserInfoDetail.getInt("bg",0);
        return i;
    }

    /*
    * 应用配置 设置字体颜色
    * */
    public static void setFontColor(int i){
        SharedPreferences.Editor editor= MyApplication.mUserInfoDetail.edit();
        editor.putInt("font_color", i);
        editor.commit();
    }
    public static int getFontColor(){
        int i=MyApplication.mUserInfoDetail.getInt("font_color",2);
        return i;
    }


}
