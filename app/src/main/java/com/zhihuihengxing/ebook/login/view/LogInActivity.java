package com.zhihuihengxing.ebook.login.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.zhihuihengxing.ebook.Dialog.DialogCreate;
import com.zhihuihengxing.ebook.R;
import com.zhihuihengxing.ebook.login.LogInContract;
import com.zhihuihengxing.ebook.login.LogInPresenter;
import com.zhihuihengxing.ebook.main.view.MainActivity;
import com.zhihuihengxing.ebook.signup.SignUpActivity;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LogInActivity extends AppCompatActivity implements LogInContract.View{

    private EditText et_log_in_username;
    private EditText et_log_in_password;
    private Button btn_log_in;
    private ImageButton iBtn_log_in_username;
    private ImageButton iBtn_log_in_password;
    private TextView tv_to_signup;

    private LogInContract.Presenter mLogInPresenter;

    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLogInPresenter=new LogInPresenter(this);
        mLogInPresenter.subscribe();
        initViews();
    }
    /*
    * 初始化Activity
    * */
    private void initViews(){
        //加载控件
        et_log_in_username=(EditText)findViewById(R.id.et_log_in_username);
        et_log_in_password=(EditText)findViewById(R.id.et_log_in_password);
        btn_log_in=(Button)findViewById(R.id.btn_log_in);
        iBtn_log_in_username=(ImageButton)findViewById(R.id.ibtn_log_in_username);
        iBtn_log_in_password=(ImageButton)findViewById(R.id.ibtn_log_in_password);
        tv_to_signup=(TextView)findViewById(R.id.tv_to_signup);
        tv_to_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
            }
        });
        iBtn_log_in_username.setVisibility(View.GONE);
        iBtn_log_in_password.setVisibility(View.GONE);
        //注册点击事件
        alertDialog= DialogCreate.createRequestingDialog(LogInActivity.this, "正在登录。。。");
        btn_log_in.setOnClickListener(new OnClickLogin());
        setEditextTextWatcher();
    }

    /*
    * OnClick
    * */
    private class OnClickLogin implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //获取登录信息
            alertDialog.show();
            String userName=et_log_in_username.getText().toString();
            String password=md5(et_log_in_password.getText().toString()).toUpperCase();
            if(userName==null||password==null){
                Toast.makeText(LogInActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            mLogInPresenter.logIn(userName, password);//登录操作
        }
    }
    /*
    * 登入成功的回调
    * */

    @Override
    public void logInSucceed(String msg) {
        //跳转到主页面
        alertDialog.hide();
        //Intent intent=new Intent(LogInActivity.this,MainActivity.class);
        //startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLogInPresenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLogInPresenter.unsubscribe();
    }

    /*
    * 登入失败的回调
    * */
    @Override
    public void logInError(String msg) {
        alertDialog.hide();
        Toast.makeText(LogInActivity.this,msg, Toast.LENGTH_SHORT).show();
    }
    /*
    * MD5加密
    * */
    private String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
    /*
   * 监听edittext是否有文字输入   以显示删除按钮
   * */
    private void setEditextTextWatcher(){

        et_log_in_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.length()>0){
                    iBtn_log_in_username.setVisibility(View.VISIBLE);
                }else{
                    iBtn_log_in_username.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_log_in_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                if(charSequence.length()>0){
                    iBtn_log_in_password.setVisibility(View.VISIBLE);
                }else{
                    iBtn_log_in_password.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

}
