package com.zhihuihengxing.ebook.utils.imageutils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by 刺雒 on 2017/1/17.
 * reqWidth需要图片的宽度
 * reqHeight需要图片的高度
 */
public class CompressBitmapUtil {
    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight){
        //图片的高度
        int height = options.outHeight;
        //图片的宽度
        int width = options.outWidth;
        //
        int targetHeight = height;
        int targetWidth = width;
        int inSampleSize = 1;
        if(height > reqHeight || width > reqWidth){
            //宽度高度按照小的来缩放
            while (targetHeight  >= reqHeight
                    && targetWidth>= reqWidth) {
                inSampleSize += 1;
                targetHeight = height/inSampleSize;
                targetWidth = width/inSampleSize;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources resources,int resID,int reqWidth,int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        //图片不压缩内存
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources,resID,options);

        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources,resID,options);
    }

    /*根据路径压缩图片*/
    public static Bitmap decodePathBitmap(String path,int reqWidth,int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);
        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);

    }

    /*根据stream*/
    public static Bitmap decodeStreamBitmap(InputStream inputStream,int reqWidth,int reqHeight) throws Exception {
        byte[] data = readStream(inputStream);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static byte[] readStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1){
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    public static Drawable decodeSampledDrawableFromResource(Resources resources,int resID,int reqWidth,int reqHeight){
        return  new BitmapDrawable(decodeSampledBitmapFromResource(resources, resID, reqWidth, reqHeight));
    }


}
