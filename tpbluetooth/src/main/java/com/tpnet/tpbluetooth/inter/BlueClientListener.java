package com.tpnet.tpbluetooth.inter;

/**
 * 链接服务相关的回调接口
 * Created by litp on 2017/6/1.
 */

public abstract class BlueClientListener {
    
 
    public void onStartConnect(){};

    public void onFinishConnect(){};

    public void onConnecting(){};

    public void onClientError(Exception e){};
 


}
