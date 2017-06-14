package com.tpnet.bluedemo.util;

import android.widget.Toast;

import com.tpnet.bluedemo.BaseApplication;

/**
 * Created by litp on 2017/6/1.
 */

public class ToastUtil {
    
    private static Toast toast;
    
    public static void show(String text){
        if(toast == null){
            toast = Toast.makeText(BaseApplication.getInstance(),text,Toast.LENGTH_SHORT);
        }
        toast.setText(text);
        toast.show();    
    }
    
    
    
}
