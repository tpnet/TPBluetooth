package com.tpnet.tpbluetooth.inter.connect;

import android.os.ParcelUuid;

import java.util.UUID;

/**
 * 
 * Created by litp on 2017/6/2.
 */

public class Constant {

    //是蓝牙通讯的一个基础常量，必须是这个UUID，，FA87C0D0-AFAC-11DE-8A39-0800200C9A66
    //信息同步服务：00001104-0000-1000-8000-00805F9B34FB
    //文件传输服务：00001106-0000-1000-8000-00805F9B34FB
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid MY_PARCELUUID_UUID = ParcelUuid.fromString("FA87C0D0-AFAC-11DE-8A39-0800200C9A66");
    public static final String SERVER_SOCKET_NAME = "BlueToothSocket";  //Socket链接的名称



    public static final int SERVER_START_LISTENER = 0x11;   //开始监听服务器
    public static final int SERVER_LISTENERING = 0x21;      //正在监听
    public static final int SERVER_FINISH_LISTENER = 0x32;  //监听完毕
    public static final int SERVER_ERROR = 0x43;            //服务器监听出现错误
    
    public static final int SERVER_REMOVE_CLIENT = 0x44;            //服务器移除一个客户端
    public static final int SERVER_CLOSE_CLIENT = 0x45;            //客户端断开Socket，被动移除客户端
    
    //有客户端进入了
    public static final int SERVER_GET_CLIENT = 0x54; 
    
    //收到消息
    public static final int RECEIVE_MESSAGE = 0x55;       //有客户端进入了


    public static final int CLIENT_START_CONNECT = 0x65;    //开始链接到服务器
    public static final int CLIENT_CONNECTING = 0x76;       //正在链接到服务器
    public static final int CLIENT_FINISH_CONNECT = 0x87;   //已经链接到服务器
    public static final int CLIENT_ERROR = 0x98;            //链接到错误
    

    public static final String INTENT_DEVICE = "intent_device"; // 
    public static final String INTENT_MESSAGE = "intent_message"; // 

    public static final long RUNNABLE_TOKEN = 0x1234;   //runnable的任务token
}
