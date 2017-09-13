package com.zhihuihengxing.ebook.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhihuihengxing.ebook.R;


public class DialogCreate {


    public static AlertDialog createRequestingDialog(Context context, String msg){
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.iv_loading_dialog_img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息

        AlertDialog loadingDialog = new AlertDialog.Builder(context).
                setView(v).setTitle("请稍等").create();// 创建自定义样式dialog

        loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        //loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
        //        LinearLayout.LayoutParams.MATCH_PARENT,
        //       LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        //loadingDialog.setView(v);
        return loadingDialog;
    }

    public static AlertDialog createLoadingDialog(Context context, String msg,DialogInterface.OnClickListener cancelListener) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.iv_loading_dialog_img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息

        AlertDialog loadingDialog = new AlertDialog.Builder(context).
                setView(v).setTitle("请稍等").
                setNegativeButton("取消", cancelListener).create();// 创建自定义样式dialog

        loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        //loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
        //        LinearLayout.LayoutParams.MATCH_PARENT,
         //       LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        //loadingDialog.setView(v);
        return loadingDialog;

    }
}
