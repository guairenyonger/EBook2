package com.zhihuihengxing.ebook.data.source;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Administrator on 2017/3/31 0031.
 */
public interface DownloadService {
    /**
     * 下载图书
     */
    @Streaming
    @GET()
    Call<ResponseBody> downloadBook(@Url String fileUrl);
}
