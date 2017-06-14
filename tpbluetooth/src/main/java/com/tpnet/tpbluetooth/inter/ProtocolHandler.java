package com.tpnet.tpbluetooth.inter;

/**
 * 处理传输协议，对数据进行封包或解包
 * Created by litp on 2017/6/2.
 */

public interface ProtocolHandler<T> {

    public byte[] encodePackage(T data);

    public T decodePackage(byte[] netData);
}

