package com.zhihuihengxing.ebook.main;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhihuihengxing.ebook.MyApplication;
import com.zhihuihengxing.ebook.bean.BookBean;
import com.zhihuihengxing.ebook.bean.CollectBookBean;
import com.zhihuihengxing.ebook.bean.RecyclerViewListItemBean;

import com.zhihuihengxing.ebook.bean.ResponseBodyBean;
import com.zhihuihengxing.ebook.data.ApiException;
import com.zhihuihengxing.ebook.data.source.BookInfoService;
import com.zhihuihengxing.ebook.data.entity.AddCollectionBody;
import com.zhihuihengxing.ebook.data.entity.CancelCollectionBody;
import com.zhihuihengxing.ebook.data.entity.GetDataByCollectionBody;
import com.zhihuihengxing.ebook.data.entity.GetDataByKindBody;
import com.zhihuihengxing.ebook.data.entity.HttpResult;
import com.zhihuihengxing.ebook.data.source.DownloadService;
import com.zhihuihengxing.ebook.utils.FileUtils;
import com.zhihuihengxing.ebook.utils.UserInfoSPUtil;
import com.zhihuihengxing.ebook.widget.progress.ProgressHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 网络请求 实现类 单例
 * Created by Administrator on 2017/3/6 0006.
 */
public class MainPresenter implements MainContract.Presenter{

    private static final String TAG="MainPresenter";

    private static final String BASE_URL=MyApplication.url+"/BookSystem/";
    private static final int DEFAULT_TIMEOUT=10;//设置超时

    private BookInfoService mBookInfoService;
    private DownloadService mDownloadService;
    private CompositeSubscription mCompositeSubscription;//订阅管理
    private Retrofit mRetrofit;
    private Retrofit mDownloadRetrofit;

    //监听者
    private MainContract.LibView mLibViewListener;
    private MainContract.BookInfoView mBookInfoViewListener;
    private MainContract.CollectionView mCollectionViewListener;
    private MainContract.BooksByKindView mBooksByKindViewListener;

    //状态值 四个状态值对应不同的view
    public static final int STATUS_NONE=0;
    public static final int  STATUS_ON_LIBVIEW=1;
    public static final int STATUS_ON_BOOKINFOVIEW=2;
    public static final int STATUS_ON_COLLECTIONVIEW=3;
    public static final int STATUS_ON_BOOKSBYKINDVIEW=4;
    public int mStatus=STATUS_NONE;//状态值


    private Gson gson=new Gson();
    /*
    * 构造方法
    * */
    public MainPresenter(MainContract.LibView libView,
                          MainContract.BookInfoView bookInfoView,
                          MainContract.CollectionView collectionView,
                         MainContract.BooksByKindView booksByKindView){

        mLibViewListener=libView;
        mCollectionViewListener=collectionView;
        mBookInfoViewListener=bookInfoView;
        mBooksByKindViewListener=booksByKindView;
    }


    /**
     * 初始化状态
     * @param status
     * @return
     */
    @Override
    public void setPresenter(int status){
        mStatus=status;//设置状态
        //return this;
    }

    /*
    * 该presenter持有两个Retrofit对象
    * */
    @Override
    public void subscribe() {
        /*第一个Retrofit对象，用户书本信息的管理*/
        //创建一个OkHttpClient
        OkHttpClient.Builder builder=new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        //配置Retrofit
        mRetrofit=new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        mBookInfoService =mRetrofit.create(BookInfoService.class);

        /*第二个Retrofit对象，用于下载资源*//*
        //配置进度框
        OkHttpClient.Builder downLoadBuilder= ProgressHelper.addProgress(null);
        downLoadBuilder.connectTimeout(DEFAULT_TIMEOUT,TimeUnit.SECONDS);

        mDownloadRetrofit=new Retrofit.Builder()
                .client(downLoadBuilder.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        mDownloadService=mDownloadRetrofit.create(DownloadService.class);*/
    }

    /**
     * 解绑所有观察者
     */
    @Override
    public void unsubscribe() {
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
    }




    /**
     *初始化书城 首页
     */
    @Override
    public void getLibData(){

        final Subscriber<String> subscriber=new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG,e.getMessage());
                switch (mStatus){
                    case STATUS_ON_LIBVIEW:
                        mLibViewListener.loadLibDataError(e.getMessage());
                        break;
                    default:
                        Log.e(TAG,"方法与状态不匹配");
                }
            }

            @Override
            public void onNext(String s) {
                switch (mStatus){
                    case STATUS_ON_LIBVIEW:
                        List<RecyclerViewListItemBean> list =
                                gson.fromJson(s, new TypeToken<List<RecyclerViewListItemBean>>() {
                        }.getType());
                        mLibViewListener.loadLibDataSucceed(list);
                        break;
                    default:
                        Log.e(TAG,"方法与状态不匹配");
                        break;
                }
            }
        };
        Observable observable=mBookInfoService.getLibData()
                .map(new HttpResultFunc<String>());
        toSubscribe(observable,subscriber);
    }

    /**
     * 收藏图书
     * @param bookID
     */
    @Override
    public void addCollection(String bookID) {


        Map<String,String> userMap= UserInfoSPUtil.loadUserInfo();
        String userID=userMap.get("userID");
        if (userID==null){
            switch (mStatus) {
                //书城页面
                case STATUS_ON_LIBVIEW:
                    mLibViewListener.addCollectionError("请先登录");
                    break;
                //图书详情页面
                case STATUS_ON_BOOKINFOVIEW:
                    mBookInfoViewListener.addCollectionError("请先登录");
                    break;
                //收藏页面
                case STATUS_ON_COLLECTIONVIEW:
                    mCollectionViewListener.addCollectionError("请先登录");
                    break;
                default:
                    Log.e(TAG, "该方法状态不对");
                    break;
            }
            return;
        }

        CollectBookBean collectBookBean=new CollectBookBean();
        collectBookBean.setBookID(bookID);
        collectBookBean.setUserID(userID);

        Gson gson = new Gson();
        String collectBookBeanJson = gson.toJson(collectBookBean);


        Map<String,String> collectBookBeanMap=new HashMap<>();
        collectBookBeanMap.put("CollectBookBean",collectBookBeanJson);

        Subscriber<String> subscriber=new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                switch (mStatus) {
                    //书城页面
                    case STATUS_ON_LIBVIEW:
                        mLibViewListener.addCollectionError(e.getMessage());
                        break;
                    //图书详情页面
                    case STATUS_ON_BOOKINFOVIEW:
                        mBookInfoViewListener.addCollectionError(e.getMessage());
                        break;
                    //收藏页面
                    case STATUS_ON_COLLECTIONVIEW:
                        mCollectionViewListener.addCollectionError(e.getMessage());
                        break;
                    default:
                        Log.e(TAG, "该方法状态不对");
                        break;
                }
            }

            @Override
            public void onNext(String s) {
                switch (mStatus) {
                    //书城页面
                    case STATUS_ON_LIBVIEW:
                        mLibViewListener.addCollectionSucceed("收藏成功");
                        break;
                    //图书详情页面
                    case STATUS_ON_BOOKINFOVIEW:
                        mBookInfoViewListener.addCollectionSucceed("收藏成功");
                        break;
                    //收藏页面
                    case STATUS_ON_COLLECTIONVIEW:
                        mCollectionViewListener.addCollectionSucceed("收藏成功");
                        break;
                    default:
                        Log.e(TAG, "该方法状态不对");
                        break;
                }
            }
        };
        Observable observable=mBookInfoService.addCollection(
                collectBookBeanJson)
                .map(new HttpResultFunc<String>());
        toSubscribe(observable, subscriber);
    }

    /**
     * 取消收藏
     * @param bookID
     */
    @Override
    public void cancelCollection(String bookID) {

        Map<String,String> userMap= UserInfoSPUtil.loadUserInfo();
        String userID=userMap.get("userID");
        if (userID==null){
            switch (mStatus) {
                //书城页面
                case STATUS_ON_LIBVIEW:
                    mLibViewListener.addCollectionError("请先登录");
                    break;
                //图书详情页面
                case STATUS_ON_BOOKINFOVIEW:
                    mBookInfoViewListener.addCollectionError("请先登录");
                    break;
                //收藏页面
                case STATUS_ON_COLLECTIONVIEW:
                    mCollectionViewListener.addCollectionError("请先登录");
                    break;
                default:
                    Log.e(TAG, "该方法状态不对");
                    break;
            }
            return;
        }

        CollectBookBean collectBookBean=new CollectBookBean();
        collectBookBean.setBookID(bookID);
        collectBookBean.setUserID(userID);

        Gson gson = new Gson();
        String collectBookBeanJson = gson.toJson(collectBookBean);


        Map<String,String> collectBookBeanMap=new HashMap<>();
        collectBookBeanMap.put("CollectBookBean",collectBookBeanJson);


        Subscriber<String> subscriber=new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                switch (mStatus) {
                    //书城页面
                    case STATUS_ON_LIBVIEW:
                        mLibViewListener.cancelCollectionError(e.getMessage());
                        break;
                    //图书详情页面
                    case STATUS_ON_BOOKINFOVIEW:
                        mBookInfoViewListener.cancelCollectionError(e.getMessage());
                        break;
                    //收藏页面
                    case STATUS_ON_COLLECTIONVIEW:
                        mCollectionViewListener.cancelCollectionError(e.getMessage());
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNext(String s) {
                switch (mStatus) {
                    //书城页面
                    case STATUS_ON_LIBVIEW:
                        mLibViewListener.cancelCollectionError("取消收藏成功");
                        break;
                    //图书详情页面
                    case STATUS_ON_BOOKINFOVIEW:
                        mBookInfoViewListener.cancelCollectionError("取消收藏成功");
                        break;
                    //收藏页面
                    case STATUS_ON_COLLECTIONVIEW:
                        mCollectionViewListener.cancelCollectionError("取消收藏成功");
                        break;
                    default:
                        break;
                }
            }
        };
        Observable observable=mBookInfoService.cancelCollection(
                collectBookBeanJson)
                .map(new HttpResultFunc<String>());
        toSubscribe(observable,subscriber);
    }
    /**
     * 根据类别请求书本信息
     * @param bookKind
     */
    @Override
    public void getDataByKind(String bookKind) {
        Subscriber<String> subscriber=new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mBooksByKindViewListener.getDataByKindError(e.getMessage());

            }

            @Override
            public void onNext(String s) {

                List<BookBean> list =
                        gson.fromJson(s, new TypeToken<List<BookBean>>() {
                        }.getType());

                 mBooksByKindViewListener.getDataByKindSucceed(list);
            }
        };
        Observable observable=mBookInfoService.getDataByKind(bookKind)
                .map(new HttpResultFunc<String>());
        toSubscribe(observable,subscriber);
    }


    /**
     * 根据用户收藏拉取书本数据
     */
    @Override
    public void getDataByCollection(){
        Subscriber<String> subscriber=new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mCollectionViewListener.getDataByCollectionError(e.getMessage());
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG,"getDataByCollection"+s);
                List<BookBean> list =
                        gson.fromJson(s, new TypeToken<List<BookBean>>() {
                        }.getType());
                    mCollectionViewListener.getDataByCollectionSucceed(list);
            }
        };
        Map<String,String> userMap= UserInfoSPUtil.loadUserInfo();
        String userID=userMap.get("userID");
        Observable observable=mBookInfoService
                .getDataByCollection(userID)
                .map(new HttpResultFunc<String>());
        toSubscribe(observable, subscriber);
    }

    /**
     * 下载图书资源
     * @param bookUrl
     * @param method 0 表示只下载 1 表示下载并且打开
     */
    @Override
    public void downloadBook(final String bookUrl, final int method) {

        OkHttpClient.Builder downLoadBuilder= ProgressHelper.addProgress(null);
        downLoadBuilder.connectTimeout(DEFAULT_TIMEOUT,TimeUnit.SECONDS);
        OkHttpClient.Builder builder=new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        mDownloadRetrofit=new Retrofit.Builder()
                .client(downLoadBuilder.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MyApplication.url+"/BookSystem/")
                .build();
        mDownloadService=mDownloadRetrofit.create(DownloadService.class);

        Log.e("bookUrl", bookUrl);

         Observable observable = Observable.create(new Observable.OnSubscribe<ResponseBodyBean>() {
                @Override
            public void call(final Subscriber<? super ResponseBodyBean> subscriber) {
                Log.e("Observable.create",Thread.currentThread()+"");
                Call<ResponseBody> call = mDownloadService.downloadBook("load?path="+bookUrl);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //Log.e("response",response+"");
                        if (response.isSuccess()) {
                            Log.d(TAG, "server contacted and has file");
                            ResponseBodyBean r=new ResponseBodyBean();
                            r.setResponse(response);
                            r.setBookName(bookUrl);
                            subscriber.onNext(r);
                        } else {
                            Log.d(TAG, "server contact failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG + "onFailure", t.getMessage());
                        subscriber.onError(new Throwable(t.getMessage()));
                    }
                });
            }
        });

        Subscriber subscriber=new Subscriber() {
            @Override
            public void onCompleted() {
                mBookInfoViewListener.downloadBookSucceed("下载成功");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG,"downloadBook onError"+e.getMessage());
                mBookInfoViewListener.downloadBookError(e.getMessage());
            }

            @Override
            public void onNext(Object o) {          //只是提示已经下载完成 或者调用View层的方法
                Log.e("onNext method",method+"");
                if (0==method) {
                    mBookInfoViewListener.downloadBookSucceed("下载完成");
                }else {
                    mBookInfoViewListener.downloadBookSucceedAndOpen("");
                }
            }
        };

        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<ResponseBodyBean>() {
                    @Override
                    public void call(ResponseBodyBean r) {
                        Log.e("call", Thread.currentThread() + "");
                        writeResponseBodyToDisk(r.getResponse().body(), r.getBookName());
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 绑定观察者
     * @param o
     * @param s
     * @param <T>
     */
    private <T> void toSubscribe(Observable<T> o, Subscriber<T> s){
        if(mCompositeSubscription==null){
            mCompositeSubscription=new CompositeSubscription();
        }
        mCompositeSubscription.add(o
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s));
    }



    /**
     * 数据直接 剥离 本文件未调用
     */
    private class HttpResultFunc<T> implements Func1<HttpResult<T>,T> {
        @Override
        public T call(HttpResult<T> tHttpResult) {
            if (tHttpResult.getState()==1){
                //错误处理
                throw new ApiException(tHttpResult.getMessage());
            }
            return tHttpResult.getDate();
        }
    }



    private boolean writeResponseBodyToDisk(ResponseBody body,String fileName) {
        try {
            // todo change the file location/name according to your needs

            String filePath = Environment.getExternalStorageDirectory().getPath()
                    + "/Ebook/";
            File fileDir = new File(filePath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            File file = new File(filePath+fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    Log.e("IOExceptionasa",e.getMessage());
                }
            }

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                Log.e("writeResponseBodyToDisk", Thread.currentThread() + "");
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                Log.e(TAG+"",e.getMessage());
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
