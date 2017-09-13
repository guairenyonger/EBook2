package com.zhihuihengxing.ebook.signup;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.zhihuihengxing.ebook.Dialog.DialogCreate;
import com.zhihuihengxing.ebook.R;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements SignUpContract.View{


    @Bind(R.id.iv_sign_up_logo)
    ImageView ivSignUpLogo;
    @Bind(R.id.et_sign_up_username)
    EditText etSignUpUsername;
    @Bind(R.id.ibtn_sign_up_username)
    ImageButton ibtnSignUpUsername;
    @Bind(R.id.et_sign_up_password)
    EditText etSignUpPassword;
    @Bind(R.id.ibtn_sign_up_password)
    ImageButton ibtnSignUpPassword;
    @Bind(R.id.et_sign_up_password2)
    EditText etSignUpPassword2;
    @Bind(R.id.ibtn_sign_up_password2)
    ImageButton ibtnSignUpPassword2;
    @Bind(R.id.tl)
    TableLayout tl;
    @Bind(R.id.btn_sign_up)
    Button btnSignUp;

    AlertDialog alertDialog;

    SignUpContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        mPresenter=new SignUpPresenter(this);
        mPresenter.subscribe();
        alertDialog= DialogCreate.createRequestingDialog(SignUpActivity.this, "正在注册。。。");
        btnSignUp.setOnClickListener(new OnClickSignUp());
        setEditextTextWatcher();
    }


    /*
    * 注册成功
    * */
    @Override
    public void signUpSucceed(String msg) {
        alertDialog.dismiss();
        Toast.makeText(SignUpActivity.this,msg, Toast.LENGTH_SHORT).show();
    }

    /*
    * 注册失败
    * */
    @Override
    public void signUpError(String msg) {
        alertDialog.dismiss();
        Toast.makeText(SignUpActivity.this,msg, Toast.LENGTH_SHORT).show();
    }

    /*
     * 点击事件
     * */
    private class OnClickSignUp implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //获取登录信息
            alertDialog.show();
            String userName=etSignUpPassword.getText().toString();
            String password=md5(etSignUpPassword.getText().toString()).toUpperCase();
            String password2=md5(etSignUpPassword2.getText().toString()).toUpperCase();
            if (etSignUpPassword.getText().toString().length()<6||etSignUpPassword.getText().toString().length()>11){
                Toast.makeText(SignUpActivity.this, "密码应该在6到11位", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                return;
            }

            if(userName==null||password==null||password2==null){
                Toast.makeText(SignUpActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                return;
            }
            if (!password.equals(password2)){
                Toast.makeText(SignUpActivity.this, "两次输入的密码不同", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                return;
            }
            mPresenter.signUp(userName, password);//登录操作
        }
    }

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

        etSignUpUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {
                    ibtnSignUpUsername.setVisibility(View.VISIBLE);
                } else {
                    ibtnSignUpUsername.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etSignUpPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                if (charSequence.length() > 0) {
                    ibtnSignUpPassword.setVisibility(View.VISIBLE);
                } else {
                    ibtnSignUpPassword.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etSignUpPassword2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                if (charSequence.length() > 0) {
                    ibtnSignUpPassword2.setVisibility(View.VISIBLE);
                } else {
                    ibtnSignUpPassword2.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
