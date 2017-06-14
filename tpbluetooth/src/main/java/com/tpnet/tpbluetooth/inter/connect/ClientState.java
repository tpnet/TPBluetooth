package com.tpnet.tpbluetooth.inter.connect;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.tpnet.tpbluetooth.inter.connect.Constant.CLIENT_CONNECTING;
import static com.tpnet.tpbluetooth.inter.connect.Constant.CLIENT_ERROR;
import static com.tpnet.tpbluetooth.inter.connect.Constant.CLIENT_FINISH_CONNECT;
import static com.tpnet.tpbluetooth.inter.connect.Constant.CLIENT_START_CONNECT;


/**
 * Created by litp on 2017/6/2.
 */

@IntDef({CLIENT_START_CONNECT,CLIENT_CONNECTING,CLIENT_FINISH_CONNECT,CLIENT_ERROR})
@Retention(RetentionPolicy.SOURCE)
public @interface ClientState{}