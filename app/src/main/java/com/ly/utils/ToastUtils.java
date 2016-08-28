package com.ly.utils;

import android.content.Context;
import android.widget.Toast;

import java.util.Stack;

/**
 * Created by LY on 2016-08-28.
 */
public class ToastUtils {

    static Toast mToast;
    public static void show(Context ctx,String content){
        if(ctx == null) return;
        mToast = Toast.makeText(ctx,content,Toast.LENGTH_SHORT);
        mToast.show();

    }

    public static void cancle(){
       if (mToast != null){
           mToast.cancel();
           mToast = null;
       }
    }
}
