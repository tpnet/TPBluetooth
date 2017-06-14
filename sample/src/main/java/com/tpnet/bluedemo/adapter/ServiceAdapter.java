package com.tpnet.bluedemo.adapter;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.tpnet.bluedemo.R;
import com.tpnet.tpbluetooth.GattAttributeResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by litp on 2017/6/14.
 */

public class ServiceAdapter extends BaseExpandableListAdapter {

     
    List<BluetoothGattService> services;
    List<List<BluetoothGattCharacteristic>> characteristics;

    public ServiceAdapter(Map<BluetoothGattService, List<BluetoothGattCharacteristic>> serviceListMap) {
        services = new ArrayList<>();
        characteristics = new ArrayList<>();
        for(BluetoothGattService service:serviceListMap.keySet()){
            services.add(service);
            characteristics.add(serviceListMap.get(service));
        }
    }

    @Override
    public int getGroupCount() {
        return services.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return characteristics.get(groupPosition).size();
    }

    @Override
    public BluetoothGattService getGroup(int groupPosition) {
        return services.get(groupPosition);
    }

    @Override
    public BluetoothGattCharacteristic getChild(int groupPosition, int childPosition) {
        return characteristics.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition+childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_father, null);
        }
        TextView tv_group = (TextView) convertView.findViewById(R.id.tv_father);
        BluetoothGattService service = getGroup(groupPosition);
     
        
        String uuid = service.getUuid().toString();
        tv_group.setText(GattAttributeResolver.getAttributeName(uuid, "未知")+": "+uuid);
        return convertView;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child, null);
        }
        RadioButton tv_group = (RadioButton) convertView.findViewById(R.id.tv_child);
        final BluetoothGattCharacteristic characteristic = getChild(groupPosition,childPosition);

        tv_group.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && buttonView.isPressed()){
                    selectCharacteristic = characteristic;
                }
            }
        });
        
        if(characteristic == selectCharacteristic){
            tv_group.setChecked(true);
        }
        
        String uuid = characteristic.getUuid().toString();

        tv_group.setText(GattAttributeResolver.getAttributeName(uuid, "未知")+": "+uuid);
        return convertView;
        
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    
    
    private BluetoothGattCharacteristic selectCharacteristic;   //当前选中的特征值
    
    
    private OnCheckListener mListener;
    
    public interface OnCheckListener{
        void onCheck(RadioButton radioButton,BluetoothGattCharacteristic characteristic);
    }
    
    public void setOnCheckListener(OnCheckListener listener){
        this.mListener = listener;
    }
    
}
