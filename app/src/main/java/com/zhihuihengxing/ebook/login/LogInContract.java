package com.zhihuihengxing.ebook.login;

import com.zhihuihengxing.ebook.BasePresenter;
import com.zhihuihengxing.ebook.BaseView;

/**
 * Created by Administrator on 2017/3/11 0011.
 */
public interface LogInContract {
    interface View{
        void logInSucceed(String msg);
        void logInError(String msg);
    }
    interface Presenter extends BasePresenter{
        void logIn(String userName,String password);
    }
}
