package com.tpnet.tpbluetooth.inter.connect;

/**
 * 
 * Created by litp on 2017/6/2.
 */

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.tpnet.tpbluetooth.inter.connect.Constant.SERVER_ERROR;
import static com.tpnet.tpbluetooth.inter.connect.Constant.SERVER_FINISH_LISTENER;
import static com.tpnet.tpbluetooth.inter.connect.Constant.SERVER_GET_CLIENT;
import static com.tpnet.tpbluetooth.inter.connect.Constant.SERVER_LISTENERING;
import static com.tpnet.tpbluetooth.inter.connect.Constant.SERVER_START_LISTENER;


@IntDef({SERVER_START_LISTENER,SERVER_LISTENERING,SERVER_FINISH_LISTENER,SERVER_ERROR,SERVER_GET_CLIENT})
@Retention(RetentionPolicy.SOURCE)
public @interface ServerState {
    
}