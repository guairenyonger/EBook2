package com.zhihuihengxing.ebook.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by 15534 on 2017/3/4.
 */

public class ScreenUtils {
    public static int getScreenWidth(Context context){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        //float density1 = dm.density;
        return dm.widthPixels;
        //int height3 = dm.heightPixels;
    }
    public static int getScreenHeight(Context context){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        //float density1 = dm.density;
        //return dm.widthPixels;
        return dm.heightPixels;
    }

}
