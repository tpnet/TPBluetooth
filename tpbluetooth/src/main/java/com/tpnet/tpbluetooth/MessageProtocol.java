package com.tpnet.tpbluetooth;

import com.tpnet.tpbluetooth.inter.ProtocolHandler;

import java.io.UnsupportedEncodingException;

/**
 * 数据传输协议的处理
 * Created by litp on 2017/6/2.
 */

public class MessageProtocol implements ProtocolHandler<String> {

    private static final String CHARSET_NAME = "utf-8";



    @Override
    public byte[] encodePackage(String data) {
        if( data == null) {
            return new byte[0];
        }
        else {
            try {
                return data.getBytes(CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return new byte[0];
            }
        }
    }

    @Override
    public String decodePackage(byte[] netData) {
        if( netData == null) {
            return "";
        }
        try {
            return new String(netData, CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
