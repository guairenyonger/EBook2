package com.zhihuihengxing.ebook.data.source;

import com.zhihuihengxing.ebook.bean.RecyclerViewListItemBean;
import com.zhihuihengxing.ebook.bean.UserInfoBean;
import com.zhihuihengxing.ebook.data.entity.HttpResult;
import com.zhihuihengxing.ebook.data.entity.LogInBody;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Administrator on 2017/3/11 0011.
 */
public interface LogInService{
    /**
     * 请求登录
     */
    @POST("Login")
    Observable<HttpResult<String>> logIn(@QueryMap Map<String, String> params);//键值对
}
