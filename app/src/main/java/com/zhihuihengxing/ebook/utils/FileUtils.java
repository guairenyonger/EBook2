package com.zhihuihengxing.ebook.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2017/3/8 0008.
 */
public class FileUtils {
    public static void saveFileFromInputStream(InputStream inputStream,String fileName)
            throws IOException {
        Log.e("FileUtils","start");
        String filePath = Environment.getExternalStorageDirectory().getPath()
                + "/Ebook/";
        FileOutputStream fileOutputStream=new FileOutputStream(filePath+fileName);
        byte[] b=new byte[1024];
        while((inputStream.read(b))!=-1){
            fileOutputStream.write(b);
            Log.e("FileUtils",b.toString());
        }
        inputStream.close();
        fileOutputStream.close();
    }
}
