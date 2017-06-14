package com.tpnet.bluedemo.adapter;

import android.view.View;
import android.widget.TextView;

import com.tpnet.bluedemo.R;
import com.tpnet.bluedemo.bean.IMMessage;
import com.tpnet.bluedemo.util.DateUtil;
import com.tpnet.bluedemo.view.BaseHolder;
import com.tpnet.bluedemo.view.DefaultAdapter;

import java.util.List;

/**
 * Created by litp on 2017/6/1.
 */

public class MessageListAdapter extends DefaultAdapter<IMMessage> {

    public final static int BASE_ITEM_TYPE_RECEIVER = 0X11;
    public final static int BASE_ITEM_TYPE_SENDER = 0X12;



    public MessageListAdapter(List<IMMessage> infos) {
        super(infos);
    }

    @Override
    public BaseHolder<IMMessage> getHolder(View v, int viewType) {

        return new ViewHolder(v);
    }

    @Override
    public int getLayoutId(int viewType) {
        if (mInfos.get(viewType).isSender()) {
            return R.layout.item_message_sender;
        } else {
            return R.layout.item_message_receiver;
        }

    }

 
    class ViewHolder extends BaseHolder<IMMessage> {

        private TextView mTvTime;
        private TextView mTvMessage;
        private TextView mTvName;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mTvMessage = (TextView) itemView.findViewById(R.id.tv_message);
            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
        }

        @Override
        public void setData(IMMessage data, int position) {
            
            mTvTime.setText(DateUtil.formatTime(data.getTime()));
            mTvName.setText(data.getName());
            mTvMessage.setText(data.getContent());
            
        }


    }


}
