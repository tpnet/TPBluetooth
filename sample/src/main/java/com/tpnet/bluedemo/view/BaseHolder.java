package com.tpnet.bluedemo.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 
 * Created by jess on 2015/11/24.
 */
public abstract class BaseHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    protected OnViewClickListener mOnViewClickListener = null;

    protected final String TAG = this.getClass().getSimpleName();

    public BaseHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);//点击事件
        itemView.setOnLongClickListener(this);
    }


    public BaseHolder(View itemView, boolean isHeaderOrFooter) {
        super(itemView);
    }

    /**
     * 设置数据
     * 刷新界面
     *
     * @param data 
     * @param position
     */
    public abstract void setData(T data, int position);


    /**
     * 释放资源
     */
    protected void onRelease() {

    }

    @Override
    public void onClick(View view) {
        if (mOnViewClickListener != null) {
            mOnViewClickListener.onViewClick(view, this.getLayoutPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnViewClickListener != null) {
            mOnViewClickListener.onViewLongClick(v, this.getLayoutPosition());
        }
        return true;
    }

    public interface OnViewClickListener {
        void onViewClick(View view, int position);

        void onViewLongClick(View viwe, int position);
    }

    public void setOnItemClickListener(OnViewClickListener listener) {
        this.mOnViewClickListener = listener;
    }
}
