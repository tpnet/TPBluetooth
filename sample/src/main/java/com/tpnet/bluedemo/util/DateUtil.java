package com.tpnet.bluedemo.util;

import java.text.SimpleDateFormat;

/**
 * Created by litp on 2017/6/1.
 */

public class DateUtil {
    
    public static String formatTime(Long time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(time);
    }
}
