package com.zhihuihengxing.ebook.signup;

import com.zhihuihengxing.ebook.BasePresenter;

/**
 * Created by Administrator on 2017/3/11 0011.
 */
public interface SignUpContract {
    interface View{
        void signUpSucceed(String msg);
        void signUpError(String msg);
    }
    interface Presenter extends BasePresenter{
        void signUp(String userName,String password);
    }
}
