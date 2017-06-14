package com.tpnet.bluedemo.view;

import android.support.annotation.IntDef;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import static com.tpnet.bluedemo.adapter.MessageListAdapter.BASE_ITEM_TYPE_RECEIVER;
import static com.tpnet.bluedemo.adapter.MessageListAdapter.BASE_ITEM_TYPE_SENDER;

/**
 * Created by jess on 2015/11/27.
 */
public abstract class DefaultAdapter<T> extends RecyclerView.Adapter<BaseHolder<T>> {
    //Recycler的Item的类型
    public static final int BASE_ITEM_TYPE_HEADER = 11111;
    public static final int BASE_ITEM_TYPE_LIST = 22222;
    public static final int BASE_ITEM_TYPE_FOOTER = 33333;



    @IntDef({BASE_ITEM_TYPE_FOOTER, BASE_ITEM_TYPE_HEADER, BASE_ITEM_TYPE_LIST,BASE_ITEM_TYPE_RECEIVER,BASE_ITEM_TYPE_SENDER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BASE_ITEM_TYPE {
    }

    //头部的View列表
    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();

    //底部的View列表
    private SparseArrayCompat<View> mFootViews = new SparseArrayCompat<>();
    //数据list
    protected List<T> mInfos;
    //点击回调
    protected OnRecyclerViewItemClickListener mOnItemClickListener = null;
    //长按回调
    protected OnRecyclerViewItemLongClickListener mOnItemLongClickListener = null;
    //ViewHolder
    private BaseHolder<T> mHolder;

    //构造器
    public DefaultAdapter(List<T> infos) {
        super();
        this.mInfos = infos;
    }

    /**
     * 移除底部View
     * @param footerView
     */
    public boolean removeFooterView(View footerView) {
        for(int i=0; i<mFootViews.size();i++){
            if(mFootViews.valueAt(i) == footerView){
                mFootViews.removeAt(i);
                return true;
            }
        }
        return false;
    }

    public boolean removeFooterView(int index) {
        return removeFooterView(mFootViews.valueAt(index));
    }

    public void removeAllFooterView(){
        mFootViews.clear();
    }
    
    public void removeAllHeaderView(){
        mHeaderViews.clear();
    }
    
 

    public boolean removeHeaderView(View headerView) {
        for(int i=0; i<mHeaderViews.size();i++){
            if(mHeaderViews.valueAt(i) == headerView){
                mHeaderViews.removeAt(i);
                return true;
            }
        }
        return false;
    }

    public boolean removeHeaderView(int index) {
        return removeHeaderView(mHeaderViews.valueAt(index));
    }

    /**
     * 创建Hodler
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public BaseHolder<T> onCreateViewHolder(ViewGroup parent, @BASE_ITEM_TYPE final int viewType) {
        if (mHeaderViews.get(viewType) != null) {
            return new VH(mHeaderViews.get(viewType));
        } else if (mFootViews.get(viewType) != null) {
            return new VH(mFootViews.get(viewType));
        }
       

        //获取View， ViewType在抽象方法处理
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(viewType), parent, false);
        //获取Holder，ViewType在抽象方法处理
        mHolder = getHolder(view, viewType);

        mHolder.setOnItemClickListener(new BaseHolder.OnViewClickListener() {//设置Item点击事件
            @Override
            public void onViewClick(View view, int position) {
                if (mOnItemClickListener != null && mInfos.size() > 0) {
                    mOnItemClickListener.onItemClick(view, viewType, mInfos.get(position-getHeadersCount()), position);
                }
            }

            @Override
            public void onViewLongClick(View view, int position) {
                if (mOnItemLongClickListener != null && mInfos.size() > 0) {
                    mOnItemLongClickListener.onItemLongClick(view, viewType, mInfos.get(position - getHeadersCount()), position);
                }
            }
        });


        return mHolder;
    }


    //底部或者头部的ViewHolder，
    private class VH extends BaseHolder {

        public VH(View itemView) {

            super(itemView, true);
        }

        @Override
        public void setData(Object data, int position) {

        }
    }

    /**
     * 设置数据
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(BaseHolder<T> holder, int position) {

        //如果是头部或者是底部的View，不处理
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            return;
        }

        holder.setData(mInfos.get(position - getHeadersCount()), position - getHeadersCount());
    }

    /**
     * 数据的个数，包含头部和底部View
     *
     * @return
     */
    @Override
    public int getItemCount() {

        return getRealItemCount() + getHeadersCount() + getFootersCount();
    }

    /**
     * 数据真实的个数
     */
    public int getRealItemCount() {
        return mInfos.size();
    }

    public List<T> getInfos() {
        return mInfos;
    }

    /**
     * 获得item的数据
     *
     * @param position
     * @return
     */
    public T getItem(int position) {
        return mInfos == null ? null : mInfos.get(position);
    }

    /**
     * 子类实现提供holder
     *
     * @param v
     * @param viewType
     * @return
     */
    public abstract BaseHolder<T> getHolder(View v, int viewType);

    /**
     * 提供Item的布局
     *
     * @param viewType
     * @return
     */
    public abstract int getLayoutId(int viewType);

    /*  ------------  头部底部View的方法开始 ------------------*/

    /**
     * 是否是头部View的位置
     * @param position
     * @return
     */
    private boolean isHeaderViewPos(int position) {
        return position < getHeadersCount();
    }

    /**
     * 是否是底部View的位置
     * @param position
     * @return
     */
    private boolean isFooterViewPos(int position) {
        return position >= getHeadersCount() + getRealItemCount();
    }

    public void addHeaderView(View view) {
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }

    public void addFootView(View view) {
        mFootViews.put(mFootViews.size() + BASE_ITEM_TYPE_FOOTER, view);
    }

    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public int getFootersCount() {
        return mFootViews.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            //根据item的位置，获取
            return mHeaderViews.keyAt(position);
        } else if (isFooterViewPos(position)) {
            return mFootViews.keyAt(position - getHeadersCount() - getRealItemCount());
        }
        
        //普通的
        
        
        return position - getHeadersCount();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        //处理多item的GridLayoutManager布局
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    if (mHeaderViews.get(viewType) != null || mFootViews.get(viewType) != null) {
                        return ((GridLayoutManager) layoutManager).getSpanCount();
                    }
                    if (spanSizeLookup != null)
                        return spanSizeLookup.getSpanSize(position);
                    return 1;
                }
            });
            gridLayoutManager.setSpanCount(gridLayoutManager.getSpanCount());
        }
    }

    @Override
    public void onViewAttachedToWindow(BaseHolder<T> holder) {
        super.onViewAttachedToWindow(holder);
        //处理多item的StaggeredGridLayoutManager布局
        int position = holder.getLayoutPosition();
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p =
                        (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }
 
    
    /*  ------------  头部底部View的方法结束 */

    /**
     * 遍历所有hodler,释放他们需要释放的资源
     *
     * @param recyclerView
     */
    public static void releaseAllHolder(RecyclerView recyclerView) {

        if (recyclerView == null) return;

        for (int i = recyclerView.getChildCount() - 1; i >= 0; i--) {
            final View view = recyclerView.getChildAt(i);
            RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
            if (viewHolder != null && viewHolder instanceof BaseHolder) {
                ((BaseHolder) viewHolder).onRelease();
            }
        }
    }

    public interface OnRecyclerViewItemClickListener<T> {
        void onItemClick(View view, int viewType, T data, int position);
    }

    //为了防止在一个页面需要多个RecyclerView点击回调，这里不添加泛型，回调Object，自行强制转换
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    public interface OnRecyclerViewItemLongClickListener<T> {
        void onItemLongClick(View view, int viewType, T data, int position);
    }


    //为了防止在一个页面需要多个RecyclerView点击回调，这里不添加泛型，回调Object，自行强制转换
    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }


}
