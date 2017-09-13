package com.zhihuihengxing.ebook.data.source;

import com.zhihuihengxing.ebook.bean.BookBean;
import com.zhihuihengxing.ebook.bean.CollectBookBean;
import com.zhihuihengxing.ebook.bean.RecyclerViewListItemBean;
import com.zhihuihengxing.ebook.data.entity.AddCollectionBody;
import com.zhihuihengxing.ebook.data.entity.GetDataByCollectionBody;
import com.zhihuihengxing.ebook.data.entity.GetDataByKindBody;
import com.zhihuihengxing.ebook.data.entity.HttpResult;
import com.zhihuihengxing.ebook.data.entity.CancelCollectionBody;


import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Administrator on 2017/3/6 0006.
 */
public interface BookInfoService {

    /*
    * 请求主页面列表
    * */
   @GET("getAllBook")
    Observable<HttpResult<String>> getLibData();
    /**
     *收藏的请求
     */
    @GET("SubmitCollectBook")
    Observable<HttpResult<String>> addCollection(@Query("CollectBookBean") String CollectBookBeanJson);
   /**
    * 取消收藏
    */
   @GET("deleteCollectBook")
   Observable<HttpResult<String>> cancelCollection(@Query("CollectBookBean") String CollectBookBeanJson);
    /**
     *按类别请求书本信息
     */
    @GET("getKindBook")
    Observable<HttpResult<String>> getDataByKind(@Query("bookKindID") String bookKindID);
   /**
    * 按用户收藏请求书本信息
    */
   @GET("getCollectBook")
   Observable<HttpResult<String>> getDataByCollection(@Query("userID") String userID);
}
