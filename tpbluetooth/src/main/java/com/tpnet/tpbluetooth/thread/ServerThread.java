package com.tpnet.tpbluetooth.thread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tpnet.tpbluetooth.inter.connect.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.tpnet.tpbluetooth.inter.connect.Constant.MY_UUID;
import static com.tpnet.tpbluetooth.inter.connect.Constant.SERVER_FINISH_LISTENER;
import static com.tpnet.tpbluetooth.inter.connect.Constant.SERVER_GET_CLIENT;
import static com.tpnet.tpbluetooth.inter.connect.Constant.SERVER_REMOVE_CLIENT;
import static com.tpnet.tpbluetooth.inter.connect.Constant.SERVER_SOCKET_NAME;

/**
 * 服务器端，等待链接
 * Created by litp on 2017/5/27.
 */

public class ServerThread extends Thread {
    
    
 
    private final BluetoothServerSocket mServerSocket; //蓝牙链接socket
    
    private final BluetoothAdapter mBluetoothAdapter;
    
    private final Handler mHandler;   //ui更新通讯
    
    private List<ConnectThread> mConnectedThreadList;  //有新的客户端连接进来的时候，在新的线程进行通讯
 
    private int mTimeout;
     
    public ServerThread(BluetoothAdapter mBluetoothAdapter, Handler mHandler,int timeout) {
        this.mBluetoothAdapter = mBluetoothAdapter;
        this.mHandler = mHandler;
        this.mTimeout = timeout;
        
        //创建服务器端的Socket
        BluetoothServerSocket cacheSocket = null;
        try{
            //cacheSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME,MY_UUID);
            cacheSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(SERVER_SOCKET_NAME,MY_UUID);
            
        }catch (IOException e){
            e.printStackTrace();
            mHandler.sendMessage(mHandler.obtainMessage(Constant.SERVER_ERROR,e));
       
        }
        
        if(cacheSocket == null){
            mHandler.sendMessage(mHandler.obtainMessage(Constant.SERVER_ERROR,new Exception("创建服务Socket为空，创建失败")));
      
        }
       
        mServerSocket = cacheSocket;

        mConnectedThreadList  = new ArrayList<>();
    }

    @Override
    public void run() {
        super.run();
        
        //告诉ui进入监听状态
        mHandler.sendEmptyMessage(Constant.SERVER_START_LISTENER);
        BluetoothSocket socket = null;
        
        
        if(mTimeout != -1){
            //循环等待客户端进入，直到出现异常
            while (true){
                try{
                    mHandler.sendEmptyMessage(Constant.SERVER_LISTENERING);
                    //下面是阻塞的，等待客户端接入
                    socket = mServerSocket.accept();
                }catch (IOException e){
                    e.printStackTrace();
                    mHandler.sendMessage(mHandler.obtainMessage(Constant.SERVER_ERROR,e));
                    mHandler.sendEmptyMessage(SERVER_FINISH_LISTENER);
                    break;
                }
                //在新线程处理
                managerConnectedSocket(socket);
                
            }
        }else{
            try{
                mHandler.sendEmptyMessage(Constant.SERVER_LISTENERING);
                //下面是阻塞的，等待客户端接入,设置了超时
                socket = mServerSocket.accept(mTimeout);
            }catch (IOException e){
                e.printStackTrace();
                mHandler.sendMessage(mHandler.obtainMessage(Constant.SERVER_ERROR,e));
                mHandler.sendEmptyMessage(SERVER_FINISH_LISTENER);
            }
            //在新线程处理
            managerConnectedSocket(socket);
        }
        
        
      
    }


    /**
     * 在新线程处理链接进来的客户端
     * @param socket 
     */
    private void managerConnectedSocket(BluetoothSocket socket) {
     
        if(socket != null){
            
            mHandler.sendMessage(mHandler.obtainMessage(SERVER_GET_CLIENT,socket.getRemoteDevice()));
            
            ConnectThread connectedThread = new ConnectThread(socket,mHandler);

            //如果存在就先取消
            if(mConnectedThreadList.contains(connectedThread)){
                Log.e("@@","存在：");
                mConnectedThreadList.get(mConnectedThreadList.indexOf(connectedThread)).cancel();
                mConnectedThreadList.remove(connectedThread);
            }
            
            mConnectedThreadList.add(connectedThread);

            //开始线程
            connectedThread.start();
            
        }

         
        
    }


    /**
     * 取消服务器的监听，回调ui完成
     */
    public void cancel(){
        
        try{
            clearAllClient();
            mServerSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        mHandler.sendEmptyMessage(SERVER_FINISH_LISTENER);
        
    }

    public void cancelClient(BluetoothDevice device){
        
        if(clearClient(device)){
            Message message = mHandler.obtainMessage(SERVER_REMOVE_CLIENT,device);
            mHandler.sendMessage(message);
        } 
    }
    
    public void sendData(byte[] data){
        
        //循环发送数据
        for(ConnectThread connectedThread:mConnectedThreadList){
            connectedThread.write(data);
        }
       
    }
    
    
    public void clearAllClient(){
        //循环发送数据
        for(ConnectThread connectedThread:mConnectedThreadList){
            connectedThread.cancel();
            mConnectedThreadList.remove(connectedThread);
        }
    }

    public boolean clearClient(BluetoothDevice device){
        //循环发送数据
        for(ConnectThread connectedThread:mConnectedThreadList){
            if(connectedThread.getRemoteDeviceMac().equals(device.getAddress())){
                connectedThread.cancel();
                mConnectedThreadList.remove(connectedThread);
                return true;
            }
        }
        
        return false;
    }


    public List<ConnectThread> getAllConnectThread() {
        return mConnectedThreadList;
    }
}
