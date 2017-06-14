package com.tpnet.tpbluetooth.thread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;

import static com.tpnet.tpbluetooth.inter.connect.Constant.CLIENT_CONNECTING;
import static com.tpnet.tpbluetooth.inter.connect.Constant.CLIENT_ERROR;
import static com.tpnet.tpbluetooth.inter.connect.Constant.CLIENT_FINISH_CONNECT;
import static com.tpnet.tpbluetooth.inter.connect.Constant.CLIENT_START_CONNECT;
import static com.tpnet.tpbluetooth.inter.connect.Constant.MY_UUID;

/**
 * 客户端链接服务端的的线程 socket类
 * Created by litp on 2017/5/27.
 */

public class ClientThread extends Thread {

    private final BluetoothSocket mSocket;   //链接的Socket
    
    private final BluetoothDevice mDevice;   //要链接的设备
    
    private BluetoothAdapter mBlueAdapter;   //本地蓝牙适配器
    
    private final Handler mHandler;       //回调主线程
    
    private ConnectThread mConnectThread;  //链接线程


    public ClientThread(BluetoothDevice device, BluetoothAdapter adapter, Handler mHandler) {

        this.mHandler = mHandler;
        this.mDevice = device;
        this.mBlueAdapter = adapter;

        BluetoothSocket cacheSocket = null;
        try {
            cacheSocket = device.createRfcommSocketToServiceRecord(MY_UUID);

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.mSocket = cacheSocket;
    }

    @Override
    public void run() {
        super.run();

        mHandler.sendEmptyMessage(CLIENT_START_CONNECT);


        //取消搜索蓝牙，
        mBlueAdapter.cancelDiscovery();
        
        try {
            //链接
            mSocket.connect();
            mHandler.sendEmptyMessage(CLIENT_CONNECTING);
        } catch (IOException e) {
            e.printStackTrace();
            mHandler.sendMessage(mHandler.obtainMessage(CLIENT_ERROR,e));
            
            try{
                mSocket.close();
            }catch (IOException e1){
                e1.printStackTrace();
            }
            mHandler.sendEmptyMessage(CLIENT_FINISH_CONNECT);
            return;
            
        }
        managerConnectedSocket(mSocket);
    }

    
    /**
     * 在新的线程链接服务器
     * @param socket
     */
    private void managerConnectedSocket(BluetoothSocket socket){
        mConnectThread = new ConnectThread(mSocket,mHandler);
        mConnectThread.start();
    }

    /**
     * 取消
     */
    public void cancel() {
        if(mConnectThread != null){
            mConnectThread.cancel();
        }
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mHandler.sendEmptyMessage(CLIENT_FINISH_CONNECT);


    }

    /**
     * 发送信息给远程设备
     *
     * @param data
     */
    public void sendData(byte[] data) {
        if(mConnectThread != null){
            mConnectThread.write(data);
        }
    }
}
