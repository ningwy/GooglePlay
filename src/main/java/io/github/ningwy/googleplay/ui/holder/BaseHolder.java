package io.github.ningwy.googleplay.ui.holder;

import android.view.View;

/**
 * ViewHolder的封装，用于：
 *  1. 加载布局
 *  2. 初始化控件
 *  3. 设置标签 setTag
 *  4. 刷新数据
 * Created by ningwy on 2016/9/4.
 */
public abstract class BaseHolder<T> {

    private T data;

    //对ListView中item的根布局
    private View mRootView;

    public BaseHolder() {
        mRootView = initView();
        /**
         * 3. 设置标签 setTag
         */
        mRootView.setTag(this);
    }

    public void setData(T data) {
        this.data = data;
        //设置数据后立马刷新数据
        refreshData(data);
    }

    public T getData() {
        return data;
    }

    /**
     * 用途：
     * 1. 加载布局
     * 2. 初始化控件
     */
    public abstract View initView();

    /**
     * 4. 刷新数据
     */
    public abstract void refreshData(T data);

    //得到根布局
    public View getRootView() {
        return mRootView;
    }

}
