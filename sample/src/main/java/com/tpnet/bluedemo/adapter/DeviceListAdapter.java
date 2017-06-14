package com.tpnet.bluedemo.adapter;

import android.bluetooth.BluetoothDevice;
import android.view.View;

import com.tpnet.bluedemo.R;
import com.tpnet.bluedemo.view.BaseHolder;
import com.tpnet.bluedemo.view.DefaultAdapter;
import com.tpnet.bluedemo.view.DeviceItemLayout;

import java.util.List;

/**
 * Created by litp on 2017/5/25.
 */

public class DeviceListAdapter extends DefaultAdapter<BluetoothDevice> {
    
    
    public DeviceListAdapter(List<BluetoothDevice> infos) {
        super(infos);
    }

    @Override
    public BaseHolder<BluetoothDevice> getHolder(View v, int viewType) {
        return new ViewHolder(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_device;
    }
    
    public class ViewHolder extends BaseHolder<BluetoothDevice>{
        
        private DeviceItemLayout  layout;

       
        public ViewHolder(View itemView) {
            super(itemView);
            layout = (DeviceItemLayout) itemView;
            //Log.e("@@","是否为空："+(layout == null));
        }

        @Override
        public void setData(BluetoothDevice data, int position) {
            layout.setData(data);
        }
    }
}
