package com.tpnet.bluedemo.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.util.UUID;

/**
 * 服务器端，等待链接
 * Created by litp on 2017/5/27.
 */

public class AcceptThread extends Thread {
    
    
    private static final String NAME = "BlueToothClass";  //Socket链接的名称
    
    //是蓝牙通讯的一个基础常量，必须是这个UUID
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BluetoothServerSocket mServerSocket; //蓝牙链接socket
    
    private final BluetoothAdapter mBluetoothAdapter;
    
    private final Handler mHandler;   //ui更新通讯
    
    private ConnectedThread mConnectedThread;   //有新的客户端连接进来的时候，在这个线程进行通讯
    
    
    
    public static final int MSG_START_LISTENER = 0x11;
    public static final int MSG_FINISH_LISTENER = 0x12;
    public static final int MSG_ERROR = 0x13;
    public static final int MSG_GET_CLIENT = 0x14; //有客户端进入了
    public static final int MSG_CONNECT_TOSERVER = 0x15;



    public AcceptThread(BluetoothAdapter mBluetoothAdapter, Handler mHandler) {
        this.mBluetoothAdapter = mBluetoothAdapter;
        this.mHandler = mHandler;
        
        //创建服务器端的Socket
        BluetoothServerSocket cacheSocket = null;
        try{
            //cacheSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME,MY_UUID);
            cacheSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME,MY_UUID);
            
        }catch (IOException e){
            e.printStackTrace();
        }
        mServerSocket = cacheSocket;
    }

    @Override
    public void run() {
        super.run();

        BluetoothSocket socket = null;
        
        
        while (true){
            
            
            try{
                //告诉ui进入监听状态
                mHandler.sendEmptyMessage(MSG_START_LISTENER);
                
                //下面是阻塞的，等待客户端接入
                socket = mServerSocket.accept();
            }catch (IOException e){
                e.printStackTrace();
                mHandler.sendMessage(mHandler.obtainMessage(MSG_ERROR,e));
                break;
            }
            
            //链接
            if(socket != null){
                managerConnectedSocket(socket);
                
                try{
                    mServerSocket.close();
                    mHandler.sendEmptyMessage(MSG_FINISH_LISTENER);
                }catch (IOException e){
                    e.printStackTrace();
                }
                break;
            }
            

        }
        
        
        
        
    }


    /**
     * 在新线程处理链接进来的客户端
     * @param socket
     */
    private void managerConnectedSocket(BluetoothSocket socket) {
        //只支持同时处理一个链接
        if(mConnectedThread != null){
            mConnectedThread.cancel();
        }
        mHandler.sendEmptyMessage(MSG_GET_CLIENT);
        mConnectedThread = new ConnectedThread(socket,mHandler);
        mConnectedThread.start();
        
    }


    /**
     * 取消服务器的监听，回调ui完成
     */
    public void cancle(){
        
        try{
            mServerSocket.close();
            mHandler.sendEmptyMessage(MSG_FINISH_LISTENER);
        }catch (IOException e){
            e.printStackTrace();
        }
        
        
        
    }
    
    public void sendData(byte[] data){
        if(mConnectedThread != null){
            mConnectedThread.write(data);
        }
    }
    
    
    
}
