package com.zhihuihengxing.ebook.data.source;

import com.zhihuihengxing.ebook.data.entity.HttpResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2017/4/6 0006.
 */
public interface SearchService {
    /*
    * 搜索搜藏的图书
    * */
    @GET("getLikeBook")
    Observable<HttpResult<String>> getDataBySearch(@Query("bookName") String bookName);
}
