package io.github.ningwy.googleplay.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.List;

import io.github.ningwy.googleplay.R;
import io.github.ningwy.googleplay.manager.ThreadManager;
import io.github.ningwy.googleplay.ui.holder.BaseHolder;
import io.github.ningwy.googleplay.ui.holder.MoreHolder;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * BaseAdapter的封装
 * Created by ningwy on 2016/9/4.
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {

    //注意：此处必须从0开始
    private static final int TYPE_MORE = 0;//加载更多布局类型
    private static final int TYPE_NORMAL = 1;//正常布局类型

    private List<T> data;

    public MyBaseAdapter(List<T> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size() + 1;
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //返回布局个数
    @Override
    public int getViewTypeCount() {
        return 2;//两种布局类型:正常布局+加载更多布局
    }

    //返回布局类型
    @Override
    public int getItemViewType(int position) {
        if (position == getCount() - 1) {//当正常布局显示最后一条数据时显示加载更多布局
            return TYPE_MORE;
        } else {
            return getInnerType(position);//否则显示正常布局
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseHolder holder;
        if (convertView == null) {
            if (getItemViewType(position) == TYPE_MORE) {
                //加载更多布局
                holder = new MoreHolder(hasMore());
            } else {
                //显示正常布局
                holder = getHolder(position);
            }
            convertView = holder.getRootView();
        } else {
            holder = (BaseHolder) convertView.getTag();
        }

        //刷新数据
        if (getItemViewType(position) != TYPE_MORE) {
            //除了加载更多布局 其他的都要刷新数据
            holder.setData(data.get(position));
        } else {
            //加载更多布局 只有在类型为加载更多时才加载更多数据
            MoreHolder moreHolder = (MoreHolder) holder;
            if (moreHolder.getData() == MoreHolder.STATE_MORE_MORE) {
                loadMore(moreHolder);
            }
        }
        return convertView;
    }

    /**
     * 交由子类去实现
     * 用于加载布局和初始化控件和设置标签
     *
     * @return BaseHolder
     */
    public abstract BaseHolder getHolder(int position);

    /**
     * 子类可以重写该方法来扩展ListView的布局数据
     *
     * @return ListView的布局数据，默认返回正常布局
     */
    public int getInnerType(int position) {
        return TYPE_NORMAL;
    }

    /**
     * 子类可以重写此方法来决定是否有加载更多布局
     * true:加载更多
     * false:没有更多
     *
     * @return 默认返回true，即默认有加载更多
     */
    public boolean hasMore() {
        return true;
    }

    private boolean isLoadMore = false;//标记是否正在加载更多数据

    private void loadMore(final MoreHolder moreHolder) {
        if (!isLoadMore) {
            isLoadMore = true;
//            new Thread() {
//                @Override
//                public void run() {
//                    final List<T> moreData = onLoadMore();
//
//                    //刷新数据需在主线程执行
//                    UIUtils.runOnUIThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (moreData != null) {
//                                //有更多数据
//                                //如果数据小于20条，则表示没有一下页数据了
//                                if (moreData.size() < 20) {
//                                    moreHolder.setData(MoreHolder.STATE_MORE_NONE);
//                                    Toast.makeText(UIUtils.getContext(), UIUtils.getString(R
//                                                    .string.no_more_data),
//                                            Toast.LENGTH_SHORT).show();
//                                } else {
//                                    moreHolder.setData(MoreHolder.STATE_MORE_MORE);
//                                }
//
//                                //添加数据
//                                data.addAll(moreData);
//                                //刷新adapter
//                                MyBaseAdapter.this.notifyDataSetChanged();
//
//                            } else {
//                                //加载出错
//                                moreHolder.setData(MoreHolder.STATE_MORE_ERROR);
//                            }
//                            isLoadMore = false;
//                        }
//                    });
//
//                }
//            }.start();

            ThreadManager.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    final List<T> moreData = onLoadMore();

                    //刷新数据需在主线程执行
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (moreData != null) {
                                //有更多数据
                                //如果数据小于20条，则表示没有一下页数据了
                                if (moreData.size() < 20) {
                                    moreHolder.setData(MoreHolder.STATE_MORE_NONE);
                                    Toast.makeText(UIUtils.getContext(), UIUtils.getString(R
                                                    .string.no_more_data),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    moreHolder.setData(MoreHolder.STATE_MORE_MORE);
                                }

                                //添加数据
                                data.addAll(moreData);
                                //刷新adapter
                                MyBaseAdapter.this.notifyDataSetChanged();

                            } else {
                                //加载出错
                                moreHolder.setData(MoreHolder.STATE_MORE_ERROR);
                            }
                            isLoadMore = false;
                        }
                    });
                }
            });
        }
    }

    /**
     * 得到数据的大小，用于分页
     * @return
     */
    public int getDataSize() {
        return data.size();
    }

    //具体的加载更多数据的方法交由子类去实现
    public abstract List<T> onLoadMore();
}
