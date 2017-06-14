package com.tpnet.tpbluetooth.inter;

/**
 * 蓝牙状态监听器
 * Created by litp on 2017/6/1.
 */

public abstract class BlueStateListener {
    
    
    public void onOpen(){};

    public void onClose(){};

    public void onOpening(){};

    public void onClosing(){};

    public void onConnected(){};

    public void onConnecting(){};

    public void onDisconnected(){};

    public void onDisconnecting(){};
 
}
