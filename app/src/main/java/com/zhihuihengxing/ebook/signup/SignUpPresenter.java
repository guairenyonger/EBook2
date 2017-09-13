package com.zhihuihengxing.ebook.signup;

import android.util.Log;

import com.google.gson.Gson;
import com.zhihuihengxing.ebook.BasePresenter;
import com.zhihuihengxing.ebook.MyApplication;
import com.zhihuihengxing.ebook.bean.UserInfoBean;
import com.zhihuihengxing.ebook.data.ApiException;
import com.zhihuihengxing.ebook.data.entity.HttpResult;
import com.zhihuihengxing.ebook.data.entity.SignUpBody;
import com.zhihuihengxing.ebook.data.source.LogInService;
import com.zhihuihengxing.ebook.data.source.SignUpService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2017/3/11 0011.
 */
public class SignUpPresenter implements SignUpContract.Presenter{

    private static final String TAG="SignUpPresenter";

    private static final String BASE_URL= MyApplication.url+"/BookSystem/";
    private static final int DEFAULT_TIMEPUT=5;

    private SignUpService mSignUpService;
    private CompositeSubscription mCompositeSubscription;//订阅管理
    private Retrofit mRetrofit;

    SignUpContract.View mSignUpViewListener;
    public SignUpPresenter(SignUpContract.View view) {
        mSignUpViewListener=view;
    }

    /*
    * 登录
    * */
    @Override
    public void signUp(String userName, String password) {
        UserInfoBean userInfoBean=new UserInfoBean();
        userInfoBean.setUserName(userName);
        userInfoBean.setPassword(password);
        Gson gson = new Gson();
        String userInfoJson = gson.toJson(userInfoBean);

        Log.e(TAG,userInfoJson);
        Map<String,String> userMap=new HashMap<>();
        userMap.put("UserInfoBean",userInfoJson);

        Subscriber<String> subscriber=new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mSignUpViewListener.signUpError(e.getMessage());
            }

            @Override
            public void onNext(String s) {
                mSignUpViewListener.signUpSucceed("注册成功");
            }
        };
        Observable<String> observable=mSignUpService.signUp(userInfoJson)
                .map(new HttpResultFunc<String>());
        toSubscribe(observable,subscriber);
    }


    @Override
    public void subscribe() {
        //创建一个OkHttpClient
        OkHttpClient.Builder builder=new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEPUT, TimeUnit.SECONDS);
        //配置Retrofit
        mRetrofit=new Retrofit.Builder().client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        mSignUpService =mRetrofit.create(SignUpService.class);
    }

    @Override
    public void unsubscribe() {
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
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
     * 数据直接 剥离
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
}
