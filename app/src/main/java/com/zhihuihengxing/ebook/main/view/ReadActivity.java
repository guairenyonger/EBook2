package com.zhihuihengxing.ebook.main.view;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zhihuihengxing.ebook.R;
import com.zhihuihengxing.ebook.utils.UserInfoSPUtil;
import com.zhihuihengxing.ebook.widget.ReadView.ReadView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReadActivity extends AppCompatActivity {

    //@Bind(R.id.et_change_font_size_pop_bottom)
    EditText etChangeFontSizePopBottom;
    //@Bind(R.id.et_change_present_pop_bottom)
    EditText etChangePresentPopBottom;
    //@Bind(R.id.btn_pop_bottom)
    Button btnPopBottom;
    /*CheckBox cbNight;*/
    //@Bind(R.id.readview_read_activity)
    //弹出菜单
    private PopupWindow mPopupWindow;
    ReadView readView;
    @Bind(R.id.ll)
    LinearLayout linearLayout;
    String uriStr;
    boolean mBoolean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        Bundle bundle = getIntent().getExtras();
        ButterKnife.bind(this);

        //隐藏状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //对于ReadView的操作
        uriStr = bundle.getString("uriStr");
        Uri uri = Uri.parse(uriStr);
        File dir = null;

        if (uriStr != null) {
            dir = new File(uri.getPath());
        }
        //setContentView(R.layout.activity_read);readView = null;
        if (dir != null) {
            readView = new ReadView(this, dir.getPath());
        } else
            finish();
        //setContentView(readView);
        readView.setOnCenterClick(new OnShowPopupWindowClick());
        /*
        * 设置字体和背景
        * */
        int font_color=UserInfoSPUtil.getFontColor();
        if (font_color==0){
            readView.setTextColor(Color.GREEN);
        }else if (font_color==1){
            readView.setTextColor(Color.LTGRAY);
        }else if (font_color==2){
            readView.setTextColor(Color.BLACK);
        }
        int bg=UserInfoSPUtil.getBg();
        if (bg==0){
            readView.setBackGround(BitmapFactory.decodeResource(getResources(), R.drawable.bg));
        }else if (bg==1){
            readView.setBackGround(BitmapFactory.decodeResource(getResources(), R.drawable.bg_white));
        }else if (bg==2){
            readView.setBackGround(BitmapFactory.decodeResource(getResources(), R.drawable.night));
        }

       /* //夜间模式
        mBoolean=UserInfoSPUtil.getNight();
        if (mBoolean){
            readView.setBackGround(BitmapFactory.decodeResource(this.getResources(), R.drawable.night));
            readView.setTextColor(Color.LTGRAY);
            readView.refresh();
        }else {

        }*/

        readView.refresh();

        linearLayout.addView(readView);
        getPopupWindow();
    }

    /*
    * 弹出下拉框
    * */
    protected void initPopupWindow() {
        final View popipWindow_view = getLayoutInflater().inflate(R.layout.pop_bottom, null, false);
        btnPopBottom=(Button)popipWindow_view.findViewById(R.id.btn_pop_bottom);
        /*cbNight=(CheckBox)popipWindow_view.findViewById(R.id.cb_night);*/
        etChangeFontSizePopBottom=(EditText)popipWindow_view.findViewById(R.id.et_change_font_size_pop_bottom);
        etChangePresentPopBottom=(EditText)popipWindow_view.findViewById(R.id.et_change_present_pop_bottom);
        //字体颜色
        RadioGroup rg_font_color=(RadioGroup)popipWindow_view.findViewById(R.id.rg_font_color);
        final RadioButton rb_green=(RadioButton)popipWindow_view.findViewById(R.id.rb_green);
        final RadioButton rb_red=(RadioButton)popipWindow_view.findViewById(R.id.rb_red);
        final RadioButton rb_black=(RadioButton)popipWindow_view.findViewById(R.id.rb_black);

        rg_font_color.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i==rb_green.getId()){
                    readView.setTextColor(Color.GREEN);
                    UserInfoSPUtil.setFontColor(0);
                }else if (i==rb_red.getId()){
                    readView.setTextColor(Color.LTGRAY);
                    UserInfoSPUtil.setFontColor(1);
                }else if (i==rb_black.getId()){
                    readView.setTextColor(Color.BLACK);
                    UserInfoSPUtil.setFontColor(2);
                }
                readView.refresh();
            }
        });

        //背景
        RadioGroup rg_bg=(RadioGroup)popipWindow_view.findViewById(R.id.rg_bg);
        final RadioButton rb_bg_paper=(RadioButton)popipWindow_view.findViewById(R.id.rb_bg_paper);
        final RadioButton rb_bg_white=(RadioButton)popipWindow_view.findViewById(R.id.rb_bg_white);
        final RadioButton rb_bg_night=(RadioButton)popipWindow_view.findViewById(R.id.rb_bg_night);

        rg_bg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i==rb_bg_paper.getId()){
                    readView.setBackGround(BitmapFactory.decodeResource(getResources(), R.drawable.bg));
                    UserInfoSPUtil.setFontColor(0);
                }else if (i==rb_bg_white.getId()){
                    readView.setBackGround(BitmapFactory.decodeResource(getResources(), R.drawable.bg_white));
                    UserInfoSPUtil.setFontColor(1);
                }else if (i==rb_bg_night.getId()){
                    readView.setBackGround(BitmapFactory.decodeResource(getResources(), R.drawable.night));
                    UserInfoSPUtil.setFontColor(2);
                }
                readView.refresh();
            }
        });

        btnPopBottom.setOnClickListener(new OnPopupWindowButtonClick());
        /*cbNight.setChecked(mBoolean);*/
        mPopupWindow = new PopupWindow(popipWindow_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popipWindow_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popipWindow_view != null && popipWindow_view.isShown()) {
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
                return false;
            }
        });
       /* cbNight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                UserInfoSPUtil.setNight(b);
                if (b){//选择夜间模式
                    readView.setBackGround(BitmapFactory.decodeResource(getResources(), R.drawable.night));
                    readView.setTextColor(Color.LTGRAY);
                }else {
                    readView.setBackGround(BitmapFactory.decodeResource(getResources(), R.drawable.bg));
                    readView.setTextColor(Color.BLACK);
                }
            }
        });*/
    }

    /**
     * 获取PopupWindow
     */
    private void getPopupWindow() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopupWindow();
        }
    }

    /**
     *下拉菜单的按钮监听
     */
    private class OnPopupWindowButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String fontSizeStr=etChangeFontSizePopBottom.getText().toString();
            String presentStr=etChangePresentPopBottom.getText().toString();
            //设置字体大小

            if(null==fontSizeStr||fontSizeStr.equals("")){
                etChangeFontSizePopBottom.setText("");
            }else{

                if (Integer.parseInt(fontSizeStr)<20||Integer.parseInt(fontSizeStr)>100) {
                    Toast.makeText(ReadActivity.this, "字体大小应在20到100之间", Toast.LENGTH_SHORT).show();
                    return;
                }
                readView.setFont_size(Integer.parseInt(fontSizeStr));
                etChangeFontSizePopBottom.setText("");
            }
            //设置进度
            if (null==presentStr||presentStr.equals("")){
                etChangePresentPopBottom.setText("");
            }else {
                if (Integer.parseInt(presentStr)<20||Integer.parseInt(presentStr)>100) {
                    Toast.makeText(ReadActivity.this, "字体大小应在20到100之间", Toast.LENGTH_SHORT).show();
                    return;
                }
                readView.setPresent(Integer.parseInt(presentStr));
                etChangePresentPopBottom.setText("");
            }
            readView.refresh();
            mPopupWindow.dismiss();
        }
    }
    /**
     * 下拉显示和隐藏的点击事件
     */
    private class OnShowPopupWindowClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            mPopupWindow.showAtLocation(view, Gravity.TOP,0,0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        readView.setOnPause();
    }
}
