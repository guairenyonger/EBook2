package com.zhihuihengxing.ebook.login;

import android.util.Log;

import com.google.gson.Gson;
import com.zhihuihengxing.ebook.MyApplication;
import com.zhihuihengxing.ebook.bean.UserInfoBean;
import com.zhihuihengxing.ebook.data.ApiException;
import com.zhihuihengxing.ebook.data.entity.HttpResult;
import com.zhihuihengxing.ebook.data.entity.LogInBody;
import com.zhihuihengxing.ebook.data.source.LogInService;
import com.zhihuihengxing.ebook.utils.UserInfoSPUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2017/3/11 0011.
 */
public class LogInPresenter implements LogInContract.Presenter{

    private static final String TAG="LogInPresenter";

    private static final String BASE_URL=MyApplication.url+"/BookSystem/";
    private static final int DEFAULT_TIMEPUT=5;

    private LogInService mLogInService;
    private CompositeSubscription mCompositeSubscription;//订阅管理
    private Retrofit mRetrofit;
    private Gson gson=new Gson();

    LogInContract.View mViewListener;
    public LogInPresenter(LogInContract.View view){
        mViewListener=view;
    }

    /*
    * 登录操作
    * */
    @Override
    public void logIn(String userName,String password) {

        if(mCompositeSubscription==null){
            mCompositeSubscription=new CompositeSubscription();
        }

        Map<String,String> userMap=new HashMap<>();
        userMap.put("userName",userName);
        userMap.put("password", password);

        Subscriber subscriber=new Subscriber() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG,e.getMessage());
                mViewListener.logInError(e.getMessage());
            }

            @Override
            public void onNext(Object o) {
                mViewListener.logInSucceed("登录成功");
            }
        };

        //运用RxJava
        mCompositeSubscription.add(
                mLogInService.logIn(userMap)//登录请求
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .map(new HttpResultFunc<String>())
                        .doOnNext(new Action1<String>() {
                            @Override
                            public void call(String str) {
                                //设置全局变量
                                UserInfoBean userInfoBean = gson.fromJson(str, UserInfoBean.class);
                                //保存到数据库中
                                UserInfoSPUtil.saveUserInfo(userInfoBean.getUserID(),userInfoBean.getUserName(),
                                    userInfoBean.getPassword());
                        }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(subscriber)
        );
    }

    /*
    * 初始化Retrofit网络框架
    * */
    @Override
    public void subscribe() {
        //创建一个OkHttpClient
        OkHttpClient.Builder builder=new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEPUT, TimeUnit.SECONDS);
        //配置Retrofit
        mRetrofit=new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        mLogInService =mRetrofit.create(LogInService.class);
    }

    /**/
    @Override
    public void unsubscribe() {
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
    }

    /**
     * 数据直接 剥离Json字符串
     */
    private class HttpResultFunc<T> implements Func1<HttpResult<T>,T> {
        @Override
        public T call(HttpResult<T> tHttpResult) {
            Log.e(TAG, tHttpResult.getDate().toString());
            if (tHttpResult.getState()==1){
                //错误处理
                throw new ApiException(tHttpResult.getMessage());
            }
            return tHttpResult.getDate();
        }
    }
}
