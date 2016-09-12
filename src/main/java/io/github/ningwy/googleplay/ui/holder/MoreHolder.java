package io.github.ningwy.googleplay.ui.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.github.ningwy.googleplay.R;
import io.github.ningwy.googleplay.utils.UIUtils;

/**
 * 加载更多的ViewHolder
 * Created by ningwy on 2016/9/4.
 */
public class MoreHolder extends BaseHolder<Integer> {

    //加载更多状态
    public static final int STATE_MORE_MORE = 1;
    //加载失败状态
    public static final int STATE_MORE_ERROR = 2;
    //没有更多数据，不会加载更多
    public static final int STATE_MORE_NONE = 3;

    private LinearLayout loadMore;
    private TextView loadError;

    public MoreHolder(boolean hasMore) {
        setData(hasMore ? STATE_MORE_MORE : STATE_MORE_NONE);
    }

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.list_view_more);
        loadMore = (LinearLayout) view.findViewById(R.id.ll_more_load);
        loadError = (TextView) view.findViewById(R.id.tv_more_error);
        return view;
    }

    @Override
    public void refreshData(Integer data) {
        switch (data) {
            //加载更多
            case STATE_MORE_MORE :
                loadMore.setVisibility(View.VISIBLE);
                loadError.setVisibility(View.GONE);
                break;

            //没有更多
            case STATE_MORE_NONE :
                loadMore.setVisibility(View.GONE);
                loadError.setVisibility(View.GONE);
                break;

            //加载失败
            case STATE_MORE_ERROR :
                loadMore.setVisibility(View.GONE);
                loadError.setVisibility(View.VISIBLE);
                break;
        }
    }
}
