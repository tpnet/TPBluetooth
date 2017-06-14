package com.tpnet.bluedemo.view;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tpnet.bluedemo.R;

/**
 * Created by litp on 2017/5/26.
 */

public class DeviceItemLayout extends LinearLayout {

    private TextView mTvName;
    private TextView mTvAddress;



    public DeviceItemLayout(Context context) {
        super(context);
    }

    public DeviceItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initView() {
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvAddress = (TextView) findViewById(R.id.tv_address);
        
    }
    
    
    public void setName(String name){
        mTvName.setText(name);
    }

    public void setAddress(String address){
        mTvAddress.setText(address);
    }

    public void setData(BluetoothDevice device) {
        if(device != null){
            setName(device.getName());
            setAddress(device.getAddress());

        }
    }
}
