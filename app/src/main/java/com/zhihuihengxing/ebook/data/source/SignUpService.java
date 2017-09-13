package com.zhihuihengxing.ebook.data.source;

import com.zhihuihengxing.ebook.bean.UserInfoBean;
import com.zhihuihengxing.ebook.data.entity.HttpResult;
import com.zhihuihengxing.ebook.data.entity.SignUpBody;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2017/3/11 0011.
 */
public interface SignUpService {
    /**
     * 请求登录
     */
    @GET("SubmitUser")
    Observable<HttpResult<String>> signUp(@Query("UserInfoBean") String userInfoBeanJson);
}
