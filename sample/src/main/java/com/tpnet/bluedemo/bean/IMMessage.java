package com.tpnet.bluedemo.bean;

/**
 * 
 * Created by litp on 2017/6/1.
 */

public class IMMessage {
    String content;  //内容
    Long time;       //时间
    
    
    String mac;  //发送者的mac
    String name;  //发送者的名字


    boolean isSender;


    public boolean isSender() {
        return isSender;
    }

    public void setSender(boolean sender) {
        isSender = sender;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
